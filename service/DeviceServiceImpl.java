package com.sploot.api.service;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.dao.DeviceRepository;
import com.sploot.api.dao.UserRepository;
import com.sploot.api.exception.BadRequestException;
import com.sploot.api.model.entity.Device;
import com.sploot.api.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.Optional;

import static com.sploot.api.util.Utility.nullCheckSetDefaultString;


@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	DeviceRepository deviceRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserService userService;

	@Override
	@Transactional
	public Device saveDeviceDetails(Device deviceRequest) throws SQLException {

		try {

			Long loggedInUserId = userService.getLoggedInUserId();
			if (!userService.isLoggedInUserAdmin() && !loggedInUserId.equals(deviceRequest.getUser().getId()))
				throw new BadRequestException("Non Admin User Can not edit some other User's details");

			Device device = new Device();
			Optional<User> optionalUser = userRepository.findById(deviceRequest.getUser().getId());
			if(optionalUser.isEmpty()) throw new BadRequestException("User Id not found");
			User user = optionalUser.get();
			device = getMostRecentDeviceRecordForPassedFields(deviceRequest);
			if (device == null) {
				device = deviceRequest;
				device.setUser(user);
			} else {
				device.setImei(nullCheckSetDefaultString(deviceRequest.getImei(), device.getImei()));
				device.setModel(StringUtils.isEmpty(deviceRequest.getModel()) ? device.getModel() : deviceRequest.getModel());
				device.setOperatingSystem(StringUtils.isEmpty(deviceRequest.getOperatingSystem()) ? device.getOperatingSystem() : deviceRequest.getOperatingSystem());
				device.setUser(deviceRequest.getUser().getId() <= 0 ? device.getUser() : user);
				device.setFireBaseId(StringUtils.isEmpty(deviceRequest.getFireBaseId()) ? device.getFireBaseId() : deviceRequest.getFireBaseId());
			}
			device.setStatus(Status.ACTIVE);
			return deviceRepository.save(device);
		} catch (Exception e) {
			log.error("Error occurred while saving the device details : {}", e);
			throw new RuntimeException("Something went wrong while saving the device details.");
		}
	}

	@Override
	public Boolean deleteDeviceDetails(Device deviceRequest) {
		Long loggedInUserId = userService.getLoggedInUserId();
		boolean isUserAdmin = userService.isLoggedInUserAdmin();
		Long userId = deviceRequest.getUser().getId();

		if ((deviceRequest.getId() == null || deviceRequest.getId() <= 0) && (userId != null && userId > 0) && !isUserAdmin && userId != loggedInUserId) {
			throw new BadRequestException("Non Admin user can not make change in other user's details");
		}
		Device device = getMostRecentDeviceRecordForPassedFields(deviceRequest);
		boolean isDeviceIdPassedValid = (deviceRequest.getId() != null && deviceRequest.getId() > 0);
		if (device == null)
			throw new BadRequestException("Device Does not exist by passed Id");
		if (!isUserAdmin && isDeviceIdPassedValid && !device.getUser().getId().equals(loggedInUserId))
			throw new BadRequestException("User is not authorised to change other user's device");
		device.setStatus(Status.DELETED);
		deviceRepository.save(device);
		return true;
	}

	private Device getMostRecentDeviceRecordForPassedFields(Device deviceRequest) {
		Long userId = deviceRequest.getUser().getId();
		User user = userRepository.findById(userId).orElse(null);
		Device deviceByFirebaseId = (StringUtils.hasLength(deviceRequest.getFireBaseId()) || user == null) ? null : deviceRepository.findByFireBaseIdAndUser(deviceRequest.getFireBaseId(), user);
		boolean isSameUserRequest = (userId > 0 && deviceByFirebaseId != null && deviceByFirebaseId.getUser().getId().equals(userId));

		// todo this logic is based on existing implementation scope of improving it.
		if(deviceRequest.getId() != null && deviceRequest.getId() > 0) return deviceRepository.findById(deviceRequest.getId()).orElse(null);
		if(isSameUserRequest) return deviceByFirebaseId;
		if (deviceRequest.getFireBaseId() != null && (userId != null && userId > 0))  {
			return deviceRepository.findFirstByUserAndFireBaseId(user, deviceRequest.getFireBaseId());
		}
		return deviceByFirebaseId;
	}
}
