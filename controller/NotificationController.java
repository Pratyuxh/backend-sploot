package com.sploot.api.controller;

import com.sploot.api.constant.enums.PostNotificationType;
import com.sploot.api.model.dto.ErrorResponseDto;
import com.sploot.api.model.dto.PushNotificationDTO;
import com.sploot.api.model.dto.ResponseDto;
import com.sploot.api.model.dto.SuccessResponseDto;
import com.sploot.api.model.entity.PushNotification;
import com.sploot.api.model.mapper.PushNotificationMapper;
import com.sploot.api.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private PushNotificationMapper pushNotificationMapper;

  @GetMapping("/notifications")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto getAllFeedNotificationOfAUser(@RequestParam(required = false, defaultValue = "50") String pageSize,
                                                   @RequestParam(required = false, defaultValue = "0") String pageNo){
    try {
      List<PushNotification> notifications =
          notificationService.getAllNotificationsOfAUser(pageSize, pageNo);

      // TODO kapil check why we are filtering out GENERAL notification.
      List<PushNotificationDTO> notificationDTOList = notifications.stream()
      .filter(notification -> !PostNotificationType.GENERAL.toString().equals(notification.getType()))
      .map(notification -> pushNotificationMapper.entityToDTO(notification))
      .collect(Collectors.toList());

      return new SuccessResponseDto<>(notificationDTOList,"Push Notifications fetched Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while retrieving notifications of a user {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }
}
