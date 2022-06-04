package com.sploot.api.controller;

import com.sploot.api.config.security.CurrentUser;
import com.sploot.api.constant.ApplicationConstant;
import com.sploot.api.model.MedicalRecordSearchFilter;
import com.sploot.api.model.dto.MedicalRecordRequestDTO;
import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.MedicalRecord;
import com.sploot.api.model.entity.MedicalRecordType;
import com.sploot.api.model.mapper.MedicalRecordMapper;
import com.sploot.api.model.mapper.MedicalRecordTypeMapper;
import com.sploot.api.service.MedicalRecordService;
import com.sploot.api.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MedicalRecordController {

  @Autowired
  private MedicalRecordService medicalRecordService;

  @Autowired
  private MedicalRecordTypeMapper medicalRecordTypeMapper;

  @Autowired
  private MedicalRecordMapper medicalRecordMapper;

  // todo have to update with admin feature of able to read any user's reminders...currently showing only logged in user's
  @GetMapping("/v1/medical/categories")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto<List<MedicalRecordTypeDTO>> getMedicalRecordTypes(@RequestParam(required = false) Long petId) {
    List<MedicalRecordType> medicalRecordTypeList;
    if (petId != null) {
      medicalRecordTypeList = medicalRecordService.findActiveMedicalRecordTypes(petId);
    } else {
      medicalRecordTypeList = medicalRecordService.findActiveMedicalRecordTypes();
    }

    if (CollectionUtils.isEmpty(medicalRecordTypeList)) return new ErrorResponseDto<>(null, "No Medical Record Type found in data store");
    List<MedicalRecordTypeDTO> medicalRecordTypeDTOS = medicalRecordTypeList.stream().map(medicalRecordType -> medicalRecordTypeMapper.entityToDTO(medicalRecordType))
            .collect(Collectors.toList());
    return new SuccessResponseDto<>(medicalRecordTypeDTOS, "Success in fetching Medical Record Types");
  }

  //TODO kapil is anyone using the v2 version.

//  @GetMapping("/v2/medical/categories")
//  public ResponseDto<List<MedicalRecordTypeDto>> getMedicalRecordTypes() {
//    List<MedicalRecordTypeDto> reminderTypeMiniDtoList = medicalRecordService.findActiveMedicalRecordTypes();
//    return (CollectionUtils.isEmpty(reminderTypeMiniDtoList)) ? new ErrorResponseDto<>(null, "No Medical Record Type found in data store") :
//            new SuccessResponseDto<>(reminderTypeMiniDtoList, "Success in fetching Medical Record Types");
//  }


  @GetMapping("/v1/medical")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto<?> getMedicalRecords(@CurrentUser Principal principal,
                                       @RequestParam(required = false) Long reminderId,
                                       @RequestParam(required = false) Long petId,
                                       @RequestParam(required = false) Long userId,
                                       @RequestParam(required = false) Long medicalRecordTypeId,
                                       @RequestParam(required = false) Boolean groupByDate,
                                       @RequestParam(required = false) Integer max,
                                       @RequestParam(required = false) Integer zoneOffset,
                                       @RequestParam(required = false) Integer offset) {
    MedicalRecordSearchFilter reminderSearchFilter = MedicalRecordSearchFilter.builder().id(reminderId).petId(petId).
            userId(userId).medicalRecordTypeId(medicalRecordTypeId).zoneOffset(zoneOffset).build();
    reminderSearchFilter.setZoneOffset((zoneOffset != null) ? zoneOffset : 0);
    reminderSearchFilter.setOffset((offset != null) ? offset : 0);
    reminderSearchFilter.setMax((max != null) ? max : ApplicationConstant.DEFAULT_PAGE_SIZE);
    groupByDate = (groupByDate != null) ? groupByDate : false;
    if (groupByDate) {
      List<MedicalRecord> medicalRecordList = medicalRecordService.findActiveMedicalRecordsForUser(reminderSearchFilter);
      // TODO kapil .. actually empty records is not an error.
      if (medicalRecordList.isEmpty()) return new ErrorResponseDto<>(null, "No records found in data store ");

      // TODO kapil .. move this to util to make it better.
      List<DateSeparatedMedicalRecordDto> mapList = new LinkedList<>();
      Map<String, List<MedicalRecordResponseDTO>> dateWiseMap =
      medicalRecordList.stream()
              .map(medicalRecord -> medicalRecordMapper.entityToDTO(medicalRecord))
              .collect(
                      Collectors.groupingBy(
                              medicalRecord ->
                                      DateTimeUtils.getDefaultDateStringFromMillisWithPassedZoneOffset(
                                              medicalRecord.getDateCreated(),
                                              zoneOffset),
                              Collectors.toList()));
      List<String> dateList = new LinkedList<>(dateWiseMap.keySet());
      dateList.sort(String::compareTo);
      dateList.forEach(
        date -> {
          List<MedicalRecordResponseDTO> medicalRecordResponseDTOS = dateWiseMap.get(date);
          medicalRecordResponseDTOS.sort((o1, o2) -> (int) (o1.getDateCreated() - o2.getDateCreated()));
          mapList.add(new DateSeparatedMedicalRecordDto(date, medicalRecordResponseDTOS));
      });

      return new SuccessResponseDto<>(dateWiseMap, "Success in fetching Medical Records");

    } else {
      List<MedicalRecord> medicalRecordList = medicalRecordService.findActiveMedicalRecordsForUser(reminderSearchFilter);
      List<MedicalRecordResponseDTO> responseDTOList = medicalRecordList.stream()
                              .map(medicalRecord -> medicalRecordMapper.entityToDTO(medicalRecord))
                              .collect(Collectors.toList());
      return (CollectionUtils.isEmpty(responseDTOList)) ? new ErrorResponseDto<>(null, "No records found in data store ") :
              new SuccessResponseDto<>(responseDTOList, "Success in fetching Medical Records");
    }
  }

  @PostMapping("/v1/medical")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto<MedicalRecordResponseDTO> storeMedicalRecord(@RequestBody MedicalRecordRequestDTO medicalRecordRequestDTO) throws IOException {
    log.debug("Start of method storeMedicalRecord params: {}",medicalRecordRequestDTO);
    MedicalRecord medicalRecordRequest = medicalRecordMapper.requestDTOToEntity(medicalRecordRequestDTO);
    MedicalRecord medicalRecord = medicalRecordService.storeMedicalRecord(medicalRecordRequest);
    return new SuccessResponseDto<>(medicalRecordMapper.entityToDTO(medicalRecord), "Medical info "
            + ((medicalRecordRequestDTO.getId() != null && medicalRecordRequestDTO.getId() > 0) ? "Updated" : "Added") + " Successfully");
  }

  @DeleteMapping("/v1/medical")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto<Boolean> deleteMedicalRecord(@RequestParam long id) {
    Boolean result = medicalRecordService.deleteMedicalRecord(id);
    if (result) {
      return new SuccessResponseDto<>( result, "Successfully Deleted Medical Record");
    } else {
      return new ErrorResponseDto<Boolean>(result, "Error while deleting Medical Record", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }
}
