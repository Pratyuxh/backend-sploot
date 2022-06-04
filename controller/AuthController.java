package com.sploot.api.controller;


import com.sploot.api.model.co.LoginRequest;
import com.sploot.api.model.dto.AuthResponseDTO;
import com.sploot.api.model.dto.AuthResponseV1DTO;
import com.sploot.api.model.dto.SignUpRequest;
import com.sploot.api.model.dto.UserPasswordDTO;
import com.sploot.api.model.entity.User;
import com.sploot.api.service.AuthService;
import com.sploot.api.service.CustomUserDetailsService;
import com.sploot.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private UserService userService;

  @GetMapping(value = "/auth/send-otp")
  public ResponseEntity<Object> sendOTP(@RequestParam(required = true) String mobileNo) {
    authService.sendOtp(mobileNo);
    return new ResponseEntity<>(null, HttpStatus.OK);
  }

//  @PostMapping(value = "/validate-otp")
//  public ResponseEntity<Map<String, String>> validateOTP(@Valid @RequestBody(required = true) OTPValidateRequest otpValidateRequest) {
//    return ResponseEntity.ok(authService.validateOTP(otpValidateRequest.getMobileNo(), otpValidateRequest.getOtp()));
//  }

  @PostMapping("/auth/login")
  public ResponseEntity<?> authenticateUserV1(@Valid @RequestBody LoginRequest loginRequest) {
    String token = authService.userLogin(loginRequest);
    return ResponseEntity.ok(new AuthResponseV1DTO(token));
  }

  // TODO: investigate whether we need location uri in response
  // TODO kapil commenting signup for now.
  @PostMapping("/auth/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    User user = authService.signup(signUpRequest);
    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/auth/login")
            .buildAndExpand(user.getId())
            .toUri();
    return ResponseEntity.created(location)
        .body("User registered successfully");
  }

  @GetMapping({"/social/{socialType}", "/auth/social/{socialType}"})
  public ResponseEntity<?> getAccessToken(@PathVariable String socialType, @RequestHeader final String token) throws UnknownHostException {
    Map<String, Object> map = new HashMap<>();
    try {
      map = customUserDetailsService.fetchAndStoreUserAndGetAccessToken(socialType, token);
      return ResponseEntity.ok(new AuthResponseDTO((String)map.get("token"), "Bearer"));

    } catch (Exception e) {
      log.error("Exception in getting user details : ", e);
      map.put("message", "Email not found .Please enter your email id.: ");
      return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
    }
  }

  // This is just a duplicate of /auth/login
  // to maintain backward compatibility.
  @RequestMapping(value= "/v1/login", consumes = {"application/x-www-form-urlencoded"})
  public @ResponseBody ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
    String token = authService.userLogin(loginRequest);
    return ResponseEntity.ok(new AuthResponseDTO(token, "Bearer"));
  }

//  @PostMapping("/v1/register")
//  @Deprecated
//  public ResponseDto<UserDTO> registerUser(@RequestBody UserRequestDTO userRequestDTO) throws IOException {
//    return userService.registerUser(userCO);
//  }


//  @PostMapping("/v1/signup")
//  public ResponseDto signupUnVerified(@RequestBody UserSignupCO signupCO) {
//    return userService.signUpUser(signupCO.getEmail());
//  }


  @PostMapping("/v1/password")
  public ResponseEntity<?> setPassword(@RequestBody UserPasswordDTO userPasswordDTO) {
    try {
      String token = authService.setPassword(userPasswordDTO.getEmail(), userPasswordDTO.getPassword(), userPasswordDTO.getOldPassword());
      return ResponseEntity.ok(new AuthResponseDTO(token, "Bearer"));
    } catch (Exception e) {
      log.error("Exception in getting user details : ", e);
      return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
    }
  }

}
