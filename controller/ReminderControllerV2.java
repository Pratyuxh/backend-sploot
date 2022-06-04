package com.sploot.api.controller;

import com.sploot.api.constant.ApplicationConstant;
import com.sploot.api.model.ReminderSearchFilter;
import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.Reminder;
import com.sploot.api.model.entity.ReminderType;
import com.sploot.api.model.mapper.ReminderMapper;
import com.sploot.api.model.mapper.ReminderTypeMapper;
import com.sploot.api.service.PetService;
import com.sploot.api.service.ReminderV2Service;
import com.sploot.api.service.UserService;
import com.sploot.api.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/v2/reminders")
@Slf4j
public class ReminderControllerV2 {

  @Autowired
  private ReminderV2Service reminderService;

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
  public ResponseEntity<?> deleteReminder(@RequestParam long id, @RequestParam(required = false, defaultValue = "false") Boolean deleteFutureRemindersAlso) throws Exception {
    reminderService.deleteReminder(id, deleteFutureRemindersAlso);
    return ResponseEntity.ok("Deleted reminder successfully!");
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto getReminders(@RequestParam(required = false) Long reminderId,
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

    // Todo kapil .. why do we need to update this?
    //userService.updateLastLogin(principal);
    ReminderSearchFilter reminderSearchFilter = ReminderSearchFilter.builder().reminderId(reminderId).petId(petId).
            userId(userId).timeAfter(timeAfter).timeBefore(timeBefore).reminderTypeId(reminderTypeId).groupByDate(groupByDate).
            medicalRecordTypeId(medicalRecordTypeId).includeCompleted(includeCompleted).zoneOffset(zoneOffset).isMedicalSection(isMedicalSection).build();
    reminderSearchFilter.setZoneOffset((zoneOffset != null) ? zoneOffset : 0);
    reminderSearchFilter.setOffset((offset != null) ? offset : 0);
    reminderSearchFilter.setMax((max != null) ? max : ApplicationConstant.DEFAULT_PAGE_SIZE);
    groupByDate = (groupByDate != null) ? groupByDate : true;
    List<Reminder> reminders = reminderService.findActiveRemindersForUser(reminderSearchFilter);
    if (reminders == null) return new ErrorResponseDto(null, "No records found in the data store");
    if (!groupByDate) {
      return new SuccessResponseDto<>(reminders, "Success in fetching Reminders");
    } else {
      Map<String, List<Reminder>> dateWiseMap = reminders.stream()
              .sorted(Comparator.comparing(Reminder::getTime))
              .collect(Collectors.groupingBy(reminder ->
                      DateTimeUtils.getDefaultDateStringFromMillisWithPassedZoneOffset(reminder.getTime(),
                              reminderSearchFilter.getZoneOffset()), Collectors.toList()));
      return new SuccessResponseDto<>(dateWiseMap, "Success in fetching Reminders");
    }
  }

//  @PutMapping(produces = {"application/json"})
//  @PreAuthorize("hasAnyRole('USER','ADMIN')")
//  public ResponseDto<ReminderDto> updateReminder(@RequestBody ReminderRequestDTO reminderCO, @RequestParam(required = false) Boolean isUpdateAll) {
//    log.info("Updating with reminder input {}", reminderCO);
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
