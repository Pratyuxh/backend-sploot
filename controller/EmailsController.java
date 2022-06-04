package com.sploot.api.controller;

import com.sploot.api.constant.enums.OtpType;
import com.sploot.api.model.co.EmailTargetCO;
import com.sploot.api.model.dto.ErrorResponseDto;
import com.sploot.api.model.dto.ResponseDto;
import com.sploot.api.model.dto.SuccessResponseDto;
import com.sploot.api.model.dto.UserOtpDTO;
import com.sploot.api.service.CommunicationService;
import com.sploot.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@RestController
@Slf4j
public class EmailsController {
    @Autowired
    private CommunicationService communicationService;
    @Autowired
    private UserService userService;

    @PostMapping("/v1/email")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto<Boolean> sendMail(@RequestBody final EmailTargetCO emailTargetCO) {
        boolean result = communicationService.sendEmail(emailTargetCO);
        return (result) ? new SuccessResponseDto<>(result, "mail sent") : new ErrorResponseDto<>(false, "Could not send email");
    }

    @GetMapping("/v1/unsubscribe")
    public String unsubscribe(@RequestParam String idUsername) {
        return communicationService.unsubscribeToEmail(idUsername);
    }

    @GetMapping("/v1/otp")
    public ResponseDto<?> getOtpResent(@RequestParam("email") @Email String userEmail, @RequestParam(value = "type", required = false) OtpType otpType) {
        boolean otpSendResult =  userService.getOtpResent(userEmail, otpType);
        return otpSendResult ? new SuccessResponseDto("Email Sent - " + userEmail, "Success in sending OTP for verification") : new ErrorResponseDto("Error in sending OTP mail for verification");
    }

    @PostMapping("/v1/otp")
    public ResponseDto<?> verifyOtp(@RequestBody UserOtpDTO dto) {
        boolean otpVerifyResult = userService.enableUser(dto.getEmail(), dto.getOtp(), dto.getType());
        return otpVerifyResult ? new SuccessResponseDto("Email Verified - " + dto.getEmail(), "Success in OTP verification") : new ErrorResponseDto("Error in verification");

    }
}
