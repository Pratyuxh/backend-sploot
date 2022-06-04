package com.sploot.api.service;


import com.sploot.api.model.entity.Device;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;


public interface DeviceService {


	@Transactional
	Device saveDeviceDetails(Device deviceRequest) throws SQLException;

	Boolean deleteDeviceDetails(Device deviceRequest);
}

