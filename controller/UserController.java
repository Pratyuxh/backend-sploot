package com.sploot.api.controller;

import com.sploot.api.config.security.CurrentUser;
import com.sploot.api.constant.enums.PersonType;
import com.sploot.api.model.dto.ErrorResponseDto;
import com.sploot.api.model.dto.ResponseDto;
import com.sploot.api.model.dto.SuccessResponseDto;
import com.sploot.api.model.dto.UserRequestDTO;
import com.sploot.api.model.entity.Address;
import com.sploot.api.model.entity.User;
import com.sploot.api.model.mapper.AddressMapper;
import com.sploot.api.model.mapper.UserMapper;
import com.sploot.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @GetMapping({"/v1/profile", "/v1/profile/user"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto getUserProfile(@RequestParam(required = false) Long userId) {
        try {
            User userProfile = userService.getUserDetailsResponse(userId);
            return new SuccessResponseDto(userMapper.entityToResponseDTO(userProfile));
        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in fetching pet profile");
        }
    }
    @PostMapping({"/v1/profile", "/v1/profile/user"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseDto saveOrUpdateProfile(@RequestBody UserRequestDTO userRequestDTO,
                                           @PathVariable(required = false) PersonType personType) {
        try {
            User userRequest = userMapper.requestDTOToEntity(userRequestDTO);
            Address addressRequest = addressMapper.userRequestDTOToAddressEntity(userRequestDTO);
            User user = userService.saveOrUpdateUserProfile(userRequest, addressRequest);
            return new SuccessResponseDto(userMapper.entityToResponseDTO(user));
        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in fetching " + personType + " profile");
        }
    }

    @RequestMapping(value = "/v1/profile", method = RequestMethod.PATCH)
    @PreAuthorize("hasRole('USER')")
    public ResponseDto patchProfile(@CurrentUser Principal principal,
                                    @RequestParam(required = true) Boolean shared){
        try {
            return new SuccessResponseDto(userService.patchProfile(principal, shared));
        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in update profile");
        }
    }
}
