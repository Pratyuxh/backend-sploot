package com.sploot.api.controller;

import com.sploot.api.model.dto.DeviceRequestDTO;
import com.sploot.api.model.dto.ErrorResponseDto;
import com.sploot.api.model.dto.ResponseDto;
import com.sploot.api.model.dto.SuccessResponseDto;
import com.sploot.api.model.entity.Device;
import com.sploot.api.model.mapper.DeviceMapper;
import com.sploot.api.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceMapper deviceMapper;

    @PostMapping("/v1/device")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto saveDeviceDetails(@RequestBody DeviceRequestDTO deviceRequestDTO) {
        log.debug("Start of method saveDeviceDetails");
        try {
            if (Objects.isNull(deviceRequestDTO)) throw new Exception("No Record Found.");
            Device device = deviceService.saveDeviceDetails(deviceMapper.requestDTOToEntity(deviceRequestDTO));
            return new SuccessResponseDto("Success in Saving device detail");
        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in saving device detail" + deviceRequestDTO);
        }
    }

    @DeleteMapping("/v1/device")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto<Boolean> deleteDeviceDetails(@RequestBody DeviceRequestDTO deviceRequestDTO) {
        log.info("In deleteDeviceDetails");
        try {
            Boolean result = deviceService.deleteDeviceDetails(deviceMapper.requestDTOToEntity(deviceRequestDTO));
            if (result) {
                return new SuccessResponseDto<>(true, "Successfully  Deleted device record");
            }
        } catch (Exception ex) {
            log.error("Error while deleting the Device : {}", ex);
        }
        return new ErrorResponseDto<>("Something went wrong while deleting the device");
    }
}
