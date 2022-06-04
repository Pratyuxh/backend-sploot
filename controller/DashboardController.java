package com.sploot.api.controller;

import com.sploot.api.model.dto.DashboardDTO;
import com.sploot.api.model.dto.ErrorResponseDto;
import com.sploot.api.model.dto.ResponseDto;
import com.sploot.api.model.dto.SuccessResponseDto;
import com.sploot.api.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto getDashboardDetails(){
        try {
            DashboardDTO dashboard = dashboardService.fetchDashboardDetails();
            return new SuccessResponseDto(dashboard,"Dashboard fetched Successfully");
        } catch(Exception ex){
            log.error("Exception occurred while fetching dashboard for user {}", ex);
            return new ErrorResponseDto<>(ex.getMessage());
        }
    }
}