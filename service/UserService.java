package com.sploot.api.service;

import com.sploot.api.config.security.UserPrincipal;
import com.sploot.api.constant.ApplicationConstant;
import com.sploot.api.constant.enums.OtpType;
import com.sploot.api.constant.enums.SocialType;
import com.sploot.api.constant.enums.Status;
import com.sploot.api.constant.enums.UserRole;
import com.sploot.api.dao.UserAuthorityRepository;
import com.sploot.api.dao.UserOtpRecordRepository;
import com.sploot.api.dao.UserRepository;
import com.sploot.api.exception.AccessDeniedException;
import com.sploot.api.exception.BadRequestException;
import com.sploot.api.exception.SplootException;
import com.sploot.api.model.entity.Address;
import com.sploot.api.model.entity.User;
import com.sploot.api.model.entity.UserAuthority;
import com.sploot.api.model.entity.UserOtpRecord;
import com.sploot.api.util.DateTimeUtils;
import com.sploot.api.util.RandomStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static com.sploot.api.util.Utility.getOtp;
import static com.sploot.api.util.Utility.nullCheckSetDefaultString;

@Service
@Slf4j
@Transactional
public class UserService {

	// TODO kapil move user service to interface impl

	@Autowired
    private UserRepository userRepository;

//	@Autowired
//	ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private CommunicationService communicationService;
//
//	@Autowired
//	private DbService dbService;
//
	@Autowired
UserAuthorityRepository userAuthorityRepository;
//
//	@Autowired
//	UserOtpRecordRepository userOtpRecordRepository;
//
//	@Autowired
//	SequenceGenerator sequenceGenerator;
//
	@Autowired
    private RandomStringUtil randomString;

	@Autowired
	private S3Serv s3Service;

	@Autowired
	Environment env;

//	@Autowired
//	TokenProvider tokenProvider;

	@Autowired
	RestTemplate restTemplate;

	@Value("${client.secret}")
	private String clientSecret;

	@Value("${client.id}")
	private String clientID;

	@Value("${protocol.is.https:false}")
	private boolean isHttps;

	@Value("${otp.validity.duration.in.millis:20000}")
	private Integer otpValidityDurationInMillis;

	@Autowired
	private AddressService addressService;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private AuthService authService;

	@Autowired
	private UserOtpRecordRepository userOtpRecordRepository;

	@Autowired
	private CommunicationService communicationService;

	@Autowired
	private UploadService uploadService;


	private Set<UserAuthority> getUserAuthoritiesStr(User user) {
		if (Objects.isNull(user.getAuthorities())) {
			UserAuthority userAuthority = userAuthorityRepository.findById((long) UserRole.USER.getAuthorityCode()).get();
			return new HashSet<>(Collections.singletonList(userAuthority));
		} else {
			List<Long> authoritiesIds = (Arrays.stream(user.getUserAuthoritiesString().split(",")).map(Long::valueOf)).collect(Collectors.toList());
			return userAuthorityRepository.findByIdIn(authoritiesIds);
		}
	}


	public Long getLoggedInUserId() {
		if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) return -1l;
		try {
			return ((UserPrincipal) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
		} catch (Exception e) {
			log.error("Error in fetching userid from principal: {}", e.getMessage());
			return -1l;
		}
	}

	public boolean signUpUser(String userEmail) {
		User dbUser = userRepository.findByEmail(userEmail).orElse(null);
		if (dbUser != null) {
			throw new BadRequestException("User already exists by this email");
		}
		User user = User.builder().userAuthorities(defaultUserAuthority())
						.userSocialId(SocialType.portal.name())
						.socialType(SocialType.portal)
						.email(userEmail)
						.enabled(false)
						.unsubscribeToEmail(false)
						.username(userEmail).build();

		user = userRepository.save(user);
		String otp = getOtpForUser(user, OtpType.USER_SIGNUP);
		return communicationService.sendOtpEmail(user, otp, OtpType.USER_SIGNUP, otpValidityDurationInMillis);
	}

	public boolean getOtpResent(String userEmail, OtpType otpType) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		if (user == null) {
			if (otpType == null || otpType == OtpType.USER_SIGNUP) {
				log.info("User will be signed Up now : {}", userEmail);
				return signUpUser(userEmail);
			}
			throw new BadRequestException("User is not registered");
		}
		if (user.isEnabled() && (otpType == OtpType.USER_SIGNUP || otpType == null))
			throw new BadRequestException("User is already a verified user.");
		String otp = getOtpForUser(user, otpType);
		return communicationService.sendOtpEmail(user, otp, otpType, otpValidityDurationInMillis);
	}

	private String getOtpForUser(User user, OtpType otpType) {
		otpType = (Objects.isNull(otpType)) ? OtpType.USER_SIGNUP : otpType;
		String otp = getOtp();
		UserOtpRecord userOtpRecord = userOtpRecordRepository.findByUserAndOtpType(user, otpType);
		if (userOtpRecord != null &&
				userOtpRecord.getValidTill() > System.currentTimeMillis()) {
			userOtpRecord.setStatus(Status.ACTIVE);
			userOtpRecord = userOtpRecordRepository.save(userOtpRecord);
			return userOtpRecord.getOtp();
		}
		if (userOtpRecord != null) {
			userOtpRecord.setOtp(otp);
			userOtpRecord.setStatus(Status.ACTIVE);
			userOtpRecord.setValidTill(DateTimeUtils.getTimeInMillisAhead(otpValidityDurationInMillis));
		} else {
			userOtpRecord = UserOtpRecord.builder().otp(otp).otpType(otpType).user(user).
					validTill(DateTimeUtils.getTimeInMillisAhead(otpValidityDurationInMillis)).build();
			userOtpRecord.setCreatedBy(ApplicationConstant.SYSTEM_ID);
			userOtpRecord.setStatus(Status.ACTIVE);
		}
		userOtpRecordRepository.save(userOtpRecord);
		return otp;
	}
	
	public boolean enableUser(String email, String otp, OtpType otpType) {
		if (otpType == null) otpType = OtpType.USER_SIGNUP;
		boolean otpVerificationResult = false;
		User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("User is not registered"));
		UserOtpRecord userOtpRecord = userOtpRecordRepository.findByUserAndOtpType(user, otpType);
		if (userOtpRecord == null)
			throw new BadRequestException("No previous entry for OTP to compare with passed OTP value");
		if (userOtpRecord.getValidTill() >= System.currentTimeMillis() && otp.equals(userOtpRecord.getOtp())) {
			log.info("User {} is verified...", email);
			user.setEnabled(true);
			// TODO check this.
			userOtpRecord.setStatus(userOtpRecord.getOtpType() == OtpType.USER_SIGNUP ?
					Status.DELETED : Status.INACTIVE);
			userOtpRecordRepository.save(userOtpRecord);
			otpVerificationResult = true;
			userRepository.save(user);
//			setMailConfig(user);
			if (otpType.equals(OtpType.USER_SIGNUP))
				communicationService.sendWelcomeEmail(user);
		}
		log.info("User with email- {}, verification status - {}", email, otpVerificationResult);
		return otpVerificationResult;
	}
	
	public List<Long> getLoggedInUserAuthorityIds(Principal principal) {
		Collection<GrantedAuthority> authorities = ((UsernamePasswordAuthenticationToken) principal).getAuthorities();
		List<Long> userAuthorityIds = new LinkedList<>();
		for (GrantedAuthority authority : authorities) {
			UserAuthority userAuthority = (UserAuthority) authority;
			userAuthorityIds.add(userAuthority.getId());
		}
		return userAuthorityIds;
	}

	public Set<UserAuthority> defaultUserAuthority() {
		UserAuthority userAuthority = userAuthorityRepository.findById((long) UserRole.USER.getAuthorityCode()).get();
		return new HashSet<>(Collections.singletonList(userAuthority));
	}

//	@Transactional
//	public ResponseDto<UserDTO> registerUser(UserCO userCO) throws IOException {
//		if (!StringUtils.isEmpty(userCO.getPassword()))
//			userCO.setPassword(passwordEncoder.encode(userCO.getPassword()));
//		RegisteredUser user = (userCO.getId() != null) ? userDao.get(userCO.getId()) : modelMapper.map(userCO, RegisteredUser.class);
//		if (user == null) user = modelMapper.map(userCO, RegisteredUser.class);
//		else {
//			user.setFirstName(userCO.getFirstName());
//			user.setPhoneNumber(userCO.getPhoneNumber());
//		}
//		user.setAddress(addressService.getAddress(userCO));
//		String uploadedPhotoUrl = getUrlAfterPhotoUpload(userCO.getPhotoBytes(),
//				com.sploot.core.utils.StringUtils.nullCheckSetDefaultString(userCO.getPhotoFormat(),
//						ApplicationConstant.JPG_FORMAT)).getUrl();
//		user.setPhotoUrl(com.sploot.core.utils.StringUtils.nullCheckSetDefaultString(uploadedPhotoUrl, user.getPhotoUrl()));
//		user.setUserAuthorities(getUserAuthoritesStr(userCO));
//		user = foo.saveUser(user);
//		return new SuccessResponseDto<>(modelMapper.map(user, UserDTO.class), "Profile updated successfully");
//	}

//	private String getReferralCodeForUser(UserCO userCO) {
//		return userCO.getUsername() != null ? userCO.getUsername() : userCO.getEmail() + (System.currentTimeMillis() % 10); // doing this so that db call is avoided at the moment. Will move to a better formula later.
//	}

//	private Set<UserAuthority> getUserAuthoritesStr(UserCO userCO) {
//		if (Objects.isNull(userCO.getAuthorities())) {
//			return defaultUserAuthority();
//		} else {
//			List<Long> authoritiesIds = (Arrays.stream(userCO.getAuthorities().split(",")).map(Long::valueOf)).collect(Collectors.toList());
//			return userAuthorityRepository.findByIdIn(authoritiesIds);
//		}
//	}


	public String getUserRole(Principal principal) throws Exception {
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) ((UsernamePasswordAuthenticationToken) principal).getAuthorities();
			log.info("principal valid Object" + principal);
			if (CollectionUtils.isEmpty(grantedAuthorities)) {
				log.error("No Authorities for passed user. {}", principal);
				throw new Exception("No Authorities for user");
			}
			List<UserAuthority> userAuthorities = grantedAuthorities.stream().map(grantedAuthority ->
					(UserAuthority) grantedAuthority).sorted(Comparator.comparingInt(UserAuthority::getWeight)).collect(Collectors.toList());
			return userAuthorities.get(0).getAuthority();
		}
		return null;
	}



//	public UserDTO getUserInformation(SocialType socialType, String token) {
//		switch (socialType) {
//			case facebook:
//				log.info("Facebook is  social type");
//				break;
//			case google:
//				log.info("Google is  social type");
//				Long userId = tokenProvider.getUserIdFromToken(token);
//				User user = userDao.get(userId);//userRepository.getOne(userId);
//				return modelMapper.map(user, UserDTO.class);
//		}
//		return null;
//	}

	private String getParamStringFromMap(String username, String password) {

		Map<String, String> tokenRequestParams = new HashMap<String, String>() {{
			put("username", username);
			put("password", password);
			put("grant_type", "password");
			put("client_id", clientID);
		}};

		StringBuilder str = new StringBuilder();
		for (String key : tokenRequestParams.keySet())
			str.append(key).append("=").append(tokenRequestParams.get(key)).append("&");
		str = new StringBuilder(str.substring(0, str.lastIndexOf("&")));
		return str.toString();
	}

	public User getUserDetailsResponse(Long userId) throws Exception {
		Long loggedInUserId = getLoggedInUserId();
		if (!userId.equals(loggedInUserId))
			throw new SplootException(
					"User is not authorised to change other user's reminder", HttpStatus.BAD_REQUEST);
		return userRepository.findById(userId).orElseThrow(() -> new AccessDeniedException("User should be registered"));
	}

	public User saveOrUpdateUserProfile(User user, Address address) throws IOException {
		Long loggedInUserId = getLoggedInUserId();
		//TODO kapil check if we need admin to create/update users.
		if (user.getId() != null && !user.getId().equals(loggedInUserId))
			throw new SplootException(
					"User is not authorised to change other user's reminder", HttpStatus.BAD_REQUEST);

		user.setId(loggedInUserId);
		if (!StringUtils.isEmpty(user.getPassword()))
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		//Todo correct this
		User dbUser = (user.getId() != null) ? userRepository.findById(user.getId()).orElse(null) : null;
		if (dbUser != null) {
			dbUser.setFirstName(user.getFirstName());
			dbUser.setPhoneNumber(user.getPhoneNumber());
			addressService.insertAddressIfNotPresent(address);
			dbUser.setAddress(address);
			if (dbUser.getPhotoBytes() != null) {
				String uploadedPhotoUrl = uploadService.getUrlAfterUpload(user.getPhotoBytes(),
						nullCheckSetDefaultString(user.getPhotoFormat(),
								ApplicationConstant.JPG_FORMAT), null).getUrl();
				dbUser.setPhotoUrl(nullCheckSetDefaultString(uploadedPhotoUrl, user.getPhotoUrl()));
			}
			dbUser.setUserAuthorities(getUserAuthoritiesStr(user));
			user = dbUser;
		}
		return userRepository.save(user);
	}
	/*

	@Override


	@Override


	private User generateUserFromEmail(String userEmail) {
		return User.builder().userAuthorities(defaultUserAuthority()).
				userSocialId(SocialType.portal.name()).socialType(SocialType.portal).email(userEmail).enabled(false).unsubscribeToEmail(false).
				username(userEmail).build();
	}

	@Override
	public ResponseDto getOtpResent(String userEmail, OtpType otpType) {
		List<User> users = userRepository.findByEmail(userEmail);
		if (CollectionUtils.isEmpty(users)) {
			log.warn("User does not exist by this email - {} ", userEmail);
			if (otpType == null || otpType == OtpType.USER_SIGNUP) {
				log.info("User will be signed Up now : {}", userEmail);
				return foo.signUpUser((userEmail));
			}
			return new ErrorResponseDto("User is not registered");
		}
		User user = users.get(0);
		if (user.isEnabled() && (otpType == OtpType.USER_SIGNUP || otpType == null))
			return new ErrorResponseDto("User is already a verified user.");
		String otp = getOtpForUser(user, otpType);
		boolean otpSendResult = communicationService.sendOtpEmail(user, otp, otpType, otpValidityDurationInMillis);
		return otpSendResult ? new SuccessResponseDto("Email Sent - " + user.getEmail(), "Success in sending OTP for verification") : new ErrorResponseDto("Error in sending OTP mail for verification");
	}

	@Override
	public ResponseDto enableUser(@NotNull UserOtpCO userOtpCO) {
		String userEmail = userOtpCO.getEmail();
		String otp = userOtpCO.getOtp();
		OtpType otpType = (userOtpCO.getType() == null) ? OtpType.USER_SIGNUP : userOtpCO.getType();
		boolean otpVerificationResult = false;
		List<User> users = userRepository.findByEmail(userEmail);
		UserOtpRecord userOtpRecord = userOtpRecordRepository.findByUserAndOtpType(users.get(0), otpType);
		if (CollectionUtils.isEmpty(users))
			return new ErrorResponseDto(userEmail, "User does not exist by this email. OTP can not be resent");
		if (userOtpRecord == null)
			return new ErrorResponseDto(userEmail, "No previous entry for OTP to compare with passed OTP value");
		User user = users.get(0);
		if (userOtpRecord.getValidTill() >= System.currentTimeMillis() && otp.equals(userOtpRecord.getOtp())) {
			log.info("User {} is verified...", userEmail);
			user.setEnabled(true);
			disableOtpEntry(userOtpRecord);
			otpVerificationResult = true;
			foo.saveUser(user);
//			setMailConfig(user);
			if (otpType.equals(OtpType.USER_SIGNUP))
				communicationService.sendWelcomeEmail(user);
		} else {
			log.info("User {} is not verified...", userEmail);
			otpVerificationResult = false;
		}
		return otpVerificationResult ? new SuccessResponseDto(userEmail, "Sign up successful!") :
				new ErrorResponseDto(userEmail, "Could not verify User,expired or wrong OTP");
	}

	/*public User setMailConfig(User user) {
		return user;
	}*/

	/*
	private void disableOtpEntry(UserOtpRecord userOtpRecord) {
		userOtpRecord.setStatus(userOtpRecord.getOtpType() == OtpType.USER_SIGNUP ?
				Status.DELETED : Status.INACTIVE);
		userOtpRecordRepository.save(userOtpRecord);
	}

	public User saveUser(User user) {
		return userDao.save(user);
	}


	public Map<String, Object> setPassword(UserPasswordCO userPasswordCO) throws InterruptedException, UnknownHostException {
		String userEmail = userPasswordCO.getEmail();
		String password = userPasswordCO.getPassword();
		List<User> users = userRepository.findByEmail(userEmail);
		Map<String, Object> map = new HashMap<>();
		if (CollectionUtils.isEmpty(users))
			throw new OAuth2Exception("User does not exist by this email." + userEmail);
		User user = users.get(0);
		if (!user.isEnabled())
			throw new OAuth2Exception("User is not enabled. Verify User through OTP first.");
		UserOtpRecord recentOtpRecord = userOtpRecordRepository.findByUserAndStatus(user, Status.INACTIVE);
		boolean isSignedUpNewlyFromPortal = StringUtils.isEmpty(user.getPassword());
		if (!isForgotPasswordCase(recentOtpRecord)) {
			log.info("Case is not of forgot password. Coming to edge conditions for resetting password.");
			if (!StringUtils.isEmpty(userPasswordCO.getOldPassword()) && !isSignedUpNewlyFromPortal
					&& (user.getSocialType() != SocialType.facebook || user.getSocialType() != SocialType.google))
				throw new OAuth2Exception("Can not change password without confirming old password");
			if (user.getSocialType() == SocialType.portal && !StringUtils.isEmpty(userPasswordCO.getOldPassword())
					&& !passwordEncoder.matches(userPasswordCO.getOldPassword(), user.getPassword()))
				throw new OAuth2Exception("Old password is not correct");
		} else {
			deleteOtpEntry(recentOtpRecord);
		}
		user.setPassword(passwordEncoder.encode(password));
		user = foo.saveUser(user);
		if (isSignedUpNewlyFromPortal)
			return getUserTokenByPassword(user.getUsername(), password);
		else
			return JacksonUtils.getObjectFromJson(JacksonUtils.toJsonMethod(new SuccessResponseDto<>("Password Successfully updated")), Map.class);

	}

	private boolean isSignupCase(UserOtpRecord userOtpRecord) {
		return (userOtpRecord != null &&
				userOtpRecord.getOtpType() == OtpType.USER_SIGNUP &&
				userOtpRecord.getStatus() == Status.INACTIVE);
	}

	public Map<String, Object> getUserTokenByPassword(String username, String password) throws UnknownHostException {
		String url = (isHttps) ? ApplicationConstant.HTTPS : ApplicationConstant.HTTP
				+ InetAddress.getLocalHost().getHostAddress() + ":" + serverPort + loginUrl + "?" + getParamStringFromMap(username, password);
		return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(null, new HttpHeaders() {{
			add("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes()));
		}}), Map.class).getBody();
	}

	private void deleteOtpEntry(UserOtpRecord recentOtpRecord) {
		if (recentOtpRecord == null) {
			log.info("No entry for otpRecord");
			return;
		}
		recentOtpRecord.setStatus(Status.DELETED);
		userOtpRecordRepository.save(recentOtpRecord);
		log.info("soft deleted otp record id {} after password change ", recentOtpRecord.getId());
	}

	private boolean isForgotPasswordCase(UserOtpRecord userOtpRecord) {
		return (userOtpRecord != null &&
				userOtpRecord.getOtpType() == OtpType.FORGOT_PASSWORD &&
				userOtpRecord.getStatus() == Status.INACTIVE);
	}


*/

	public boolean isLoggedInUserAdmin() {
		UserPrincipal userPrincipal = ((UserPrincipal) (SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
		return isUserAdmin(userPrincipal);
	}
	public boolean isUserAdmin(Principal principal) {
		List<UserAuthority> userAuthorities = getLoggedInUserAuthorities(principal);
		UserAuthority adminAuthority = userAuthorityRepository.findByName(ApplicationConstant.ADMIN_ROLE);
		log.info(">>>User authority {} with database authority {}",userAuthorities,adminAuthority);
		if(adminAuthority!=null)
		{
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("Updated values are :{}",getLoggedInUserAuthorities(principal));

		}
		userAuthorities = getLoggedInUserAuthorities(principal);
		log.info(">>>User authority {} with database authority {}",userAuthorities,adminAuthority);
		return userAuthorities.contains(adminAuthority);
	}

	public boolean isUserAdmin(UserPrincipal principal) {
		List<UserAuthority> userAuthorities = (List<UserAuthority>) principal.getAuthorities();
		UserAuthority adminAuthority = userAuthorityRepository.findByName(ApplicationConstant.ADMIN_ROLE);
		return userAuthorities.contains(adminAuthority);
	}
/*
	public String unsubscribeToEmail(String idUsername) {
		try {
			byte[] base64decodedBytes = Base64.getMimeDecoder().decode(idUsername);
			String decodedString = new String(base64decodedBytes, "utf-8");
			String[] splitIdAndUsername = decodedString.split(":");
			User users = userRepository.getOne(Long.valueOf(splitIdAndUsername[0]));
			if (users != null) {
				users.setUnsubscribeToEmail(true);
				userRepository.save(users);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "<html>You are succcessfully unsubscribed !</html>";
	}
	*/

	public void updateLastLogin(Principal principal) {
		log.info("Updating last login time for user : {}", principal.getName());
		User user = userRepository.findByUsername(principal.getName());
		if (user.getLastLoggedIn() == null || (user.getLastLoggedIn() != null &&
				((System.currentTimeMillis() - user.getLastLoggedIn()) > ApplicationConstant.LAST_LOGIN_UPDATE_THRESHOLD))) {
			user.setLastLoggedIn(System.currentTimeMillis());
			userRepository.saveAndFlush(user);
		}
	}

	private List<UserAuthority> getLoggedInUserAuthorities(Principal principal) {
		Collection<GrantedAuthority> authorities = ((UsernamePasswordAuthenticationToken) principal).getAuthorities();
		return authorities.stream().map(grantedAuthority -> (UserAuthority) grantedAuthority).collect(Collectors.toList());

	}

	public User patchProfile(Principal principal, Boolean shared){
		Long userId = getLoggedInUserId();
		User user = userRepository.getOne(userId);
		user.setHasSharedPetProfile(shared);
		return userRepository.save(user);
	}

/*
	private String getOtpForUser(User user, OtpType otpType) {
		otpType = (Objects.isNull(otpType)) ? OtpType.USER_SIGNUP : otpType;
		String otp = getOtp();
		UserOtpRecord userOtpRecord = userOtpRecordRepository.findByUserAndOtpType(user, otpType);
		if (userOtpRecord != null &&
				userOtpRecord.getValidTill() > System.currentTimeMillis()) {
			userOtpRecord.setStatus(Status.ACTIVE);
			userOtpRecord = userOtpRecordRepository.save(userOtpRecord);
			return userOtpRecord.getOtp();
		}
		if (userOtpRecord != null) {
			userOtpRecord.setOtp(otp);
			userOtpRecord.setStatus(Status.ACTIVE);
			userOtpRecord.setValidTill(DateTimeUtils.getTimeInMillisAhead(otpValidityDurationInMillis));
		} else {
			userOtpRecord = UserOtpRecord.builder().otp(otp).otpType(otpType).user(user).
					validTill(DateTimeUtils.getTimeInMillisAhead(otpValidityDurationInMillis)).build();
			userOtpRecord.setCreatedBy(ApplicationConstant.SYSTEM_ID);
			userOtpRecord.setStatus(Status.ACTIVE);
		}
		userOtpRecordRepository.save(userOtpRecord);
		return otp;
	}

	private String getOtp() {
		return new RandomStringUtil(ApplicationConstant.OTP_DEFAULT_LENGTH, new SecureRandom(), RandomStringUtil.digits).nextString();
	}

	private Map<String, String> getClientAuthHeader() {
		return new HashMap<String, String>() {{
			put("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes()));
		}};
	}

	public Map<String, Object> populateFieldsForGoogle(String token, Map<String, Object> attributes) throws OAuth2AuthenticationException {
		String emailEndpointUri = env.getProperty("google.resource.userInfoUri");
		Assert.notNull(emailEndpointUri, "LinkedIn email address end point required");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		HttpEntity<?> entity = new HttpEntity<>("", headers);
		ResponseEntity<Map> response = restTemplate.exchange(emailEndpointUri, HttpMethod.GET, entity, Map.class);
		List<?> list = (List<?>) response.getBody().get("elements");
		Map map = (Map<?, ?>) ((Map<?, ?>) list.get(0)).get("handle~");
		attributes.putAll(map);
		return map;
	}

	private File writeBytes(byte[] bytes, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			os.write(bytes);
			log.info("Successfully byte inserted");
			os.close();
		} catch (Exception e) {
			log.info("Exception: " + e);
		}
		return file;
	}
	*/
}
