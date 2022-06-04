package com.sploot.api.controller;

import com.sploot.api.config.security.CurrentUser;
import com.sploot.api.constant.ApplicationConstant;
import com.sploot.api.model.ReminderSearchFilter;
import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.Reminder;
import com.sploot.api.model.entity.ReminderType;
import com.sploot.api.model.mapper.ReminderMapper;
import com.sploot.api.model.mapper.ReminderTypeMapper;
import com.sploot.api.service.PetService;
import com.sploot.api.service.ReminderService;
import com.sploot.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/v1/reminders")
public class ReminderController {

  @Autowired
  private ReminderService reminderService;

  @Autowired
  private ReminderMapper reminderMapper;

  @Autowired
  private ReminderTypeMapper reminderTypeMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private PetService petService;

  @GetMapping("/categories")
  public ResponseDto<?> getReminderTypes() {
    List<ReminderType> reminderTypes = reminderService.findActiveReminderTypes();
    if (reminderTypes.isEmpty()) return new ErrorResponseDto<Void>(null, "No reminderCategories found in data store");
    List<ReminderTypeDTO> reminderTypeDTOS =
            reminderTypes.stream().map((result) -> reminderTypeMapper.entityToResponseDTO(result))
                    .collect(Collectors.toList());
    Map<String, List<ReminderTypeDTO>> result =  reminderTypeDTOS.stream()
            .collect(
                    Collectors.groupingBy(
                            reminderTypeDto -> reminderTypeDto.getReminderCategory().getDescription(),
                            Collectors.toList()));
    return new SuccessResponseDto<>(result, "Success in fetching Reminders Categories");
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<ReminderDto> storeReminder(@RequestBody ReminderRequestDTO reminderRequestDTO) throws Exception {

    Reminder reminderRequest = reminderMapper.requestDTOToEntity(reminderRequestDTO);
    Reminder reminder = reminderService.createReminder(reminderRequest);
    return ResponseEntity.ok(reminderMapper.entityToResponseDTO(reminder));
  }

  @DeleteMapping
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<?> deleteReminder(@CurrentUser Principal principal, @RequestParam long id, @RequestParam(required = false, defaultValue = "false") Boolean deleteFutureRemindersAlso) throws Exception {
    reminderService.deleteReminder(principal, id, deleteFutureRemindersAlso);
    return ResponseEntity.ok("Deleted reminder successfully!");
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto getReminders(@CurrentUser Principal principal,
      @RequestParam(required = false) Long reminderId,
      @RequestParam(required = false) Long reminderTypeId,
      @RequestParam(required = false) Long medicalRecordTypeId,
      @RequestParam(required = false) Long petId,
      @RequestParam(required = false) Long userId,
      @RequestParam(required = false) Long timeBefore,
      @RequestParam(required = false) Boolean groupByDate,
      @RequestParam(required = false) Integer zoneOffset,
      @RequestParam(required = false) boolean includeCompleted,
      @RequestParam(required = false) Integer max,
      @RequestParam(required = false, name = "isMedicalRecordRequest") Boolean isMedicalSection,
      @RequestParam(required = false) Integer offset,
      @RequestParam(required = false) Long timeAfter) {

    userService.updateLastLogin(principal);
    ReminderSearchFilter reminderSearchFilter = ReminderSearchFilter.builder().reminderId(reminderId).petId(petId).
            userId(userId).timeAfter(timeAfter).timeBefore(timeBefore).reminderTypeId(reminderTypeId).groupByDate(groupByDate).
            medicalRecordTypeId(medicalRecordTypeId).includeCompleted(includeCompleted).zoneOffset(zoneOffset).isMedicalSection(isMedicalSection).build();
    reminderSearchFilter.setZoneOffset((zoneOffset != null) ? zoneOffset : 0);
    reminderSearchFilter.setOffset((offset != null) ? offset : 0);
    reminderSearchFilter.setMax((max != null) ? max : ApplicationConstant.DEFAULT_PAGE_SIZE);

    groupByDate = (groupByDate != null) ? groupByDate : true;
    if (!groupByDate) {
      List<ReminderDto> reminderTypeDtoList = reminderService.findActiveRemindersForUser(reminderSearchFilter, principal);
      return (CollectionUtils.isEmpty(reminderTypeDtoList)) ? new ErrorResponseDto<>(null, "No records found in data store ") :
              new SuccessResponseDto<>(reminderTypeDtoList, "Success in fetching Reminders");
    } else {
      List<DateSeparatedReminderDTO> reminderTypeDtoList = reminderService.findDateSeparatedActiveRemindersForUser(reminderSearchFilter, principal);
      return (CollectionUtils.isEmpty(reminderTypeDtoList)) ? new ErrorResponseDto<>(null, "No reminders found in data store ") :
              new SuccessResponseDto<>(reminderTypeDtoList, "Success in fetching Reminders");
    }
  }

//  @PutMapping(produces = {"application/json"})
//  @PreAuthorize("hasAnyRole('USER','ADMIN')")
//  public ResponseDto<ReminderDto> updateReminder(@CurrentUser Principal principal, @RequestBody ReminderCO reminderCO, @RequestParam(required = false) Boolean isUpdateAll) {
//    log.info("Updating the reminder for user : {} with reminder input {}", principal, reminderCO);
//    try {
//      ReminderDto response = reminderService.updateReminder(reminderCO, BooleanUtils.isTrue(isUpdateAll));
//      return new SuccessResponseDto<>(modelMapper.map(response, ReminderDto.class), "Reminder updated successfully");
//    } catch (Exception exception) {
//      log.error("Exception in updating the reminder for user {} with reminder {}", principal, reminderCO, exception);
//      return new ErrorResponseDto<>("User is not authorised to change other user's reminder");
//    }
//  }

//  @Autowired
//  ReminderNotificationEventConsumer reminderNotificationEventConsumer;
//
//  @Value("${firebase.notification.push.url}")
//  private String firebaseUrl;
//  @Value("${firebase.notification.push.token}")
//  private String firebaseToken;
//  @Autowired
//  RestTemplate restTemplate;
//
//  @Autowired
//  ReminderNextTimeJob reminderNextTimeJob;
//
//  @GetMapping("/v1/reminderNextTimeJob")
//  @Deprecated
//  @PreAuthorize("hasAnyRole('USER','ADMIN')")//todo change role later to only admin
//  public String reminderNextTimeJobExecute() throws JobExecutionException {
//    long startTime = System.currentTimeMillis();
//    reminderNextTimeJob.execute();
//    return "Success in " + (System.currentTimeMillis() - startTime);
//  }

}
