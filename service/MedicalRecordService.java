package com.sploot.api.service;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.dao.MedicalRecordRepository;
import com.sploot.api.dao.MedicalRecordTypeRepository;
import com.sploot.api.dao.PetProfileRepository;
import com.sploot.api.dao.UserRepository;
import com.sploot.api.exception.AccessDeniedException;
import com.sploot.api.exception.BadRequestException;
import com.sploot.api.exception.NotFoundException;
import com.sploot.api.exception.SplootException;
import com.sploot.api.model.MedicalRecordSearchFilter;
import com.sploot.api.model.FileUpload;
import com.sploot.api.model.entity.MedicalRecord;
import com.sploot.api.model.entity.MedicalRecordType;
import com.sploot.api.model.entity.PetProfile;
import com.sploot.api.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class MedicalRecordService {

  @Autowired
  private MedicalRecordTypeRepository medicalRecordTypeRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  @Autowired
  private S3service s3service;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PetProfileRepository petProfileRepository;


  public List<MedicalRecordType> findActiveMedicalRecordTypes(Long petId) {
    Long userId = userService.getLoggedInUserId();
    List<MedicalRecordType> medicalRecordType =this.findActiveMedicalRecordTypes();

    // TODO kapil - check why this is required.
    medicalRecordType.forEach(medicalRecordTypeDto -> updateWithRecentEntry(userId, medicalRecordTypeDto, petId));
    return medicalRecordType;
  }

  public List<MedicalRecordType> findActiveMedicalRecordTypes() {
    return medicalRecordTypeRepository.findByStatus(Status.ACTIVE);
  }

  private void updateWithRecentEntry(Long userId, MedicalRecordType medicalRecordType, Long petId) {
    MedicalRecord medicalRecord = medicalRecordRepository.findOneByUserAndMedicalRecordTypeAndStatus(userId, medicalRecordType.getId(), Status.ACTIVE.ordinal());
    if (medicalRecord != null && ((petId == null) || medicalRecord.getPetProfile().getId().equals(petId))) {
      medicalRecordType.setRecentEntry(medicalRecord.getText());
      medicalRecordType.setCreationTime(medicalRecord.getDateCreated());
    }
  }

  public List<MedicalRecord> findActiveMedicalRecordsForUser(MedicalRecordSearchFilter medicalRecordSearchFilter) {
    Long userId = medicalRecordSearchFilter.getUserId();
    long loggedInUserId = userService.getLoggedInUserId();
    boolean isUserIdPassedValid =
        medicalRecordSearchFilter.getUserId() != null && medicalRecordSearchFilter.getUserId() > 0;
    if (userId == null || userId <= 0) medicalRecordSearchFilter.setUserId(loggedInUserId);
    if (isUserIdPassedValid && !userService.isLoggedInUserAdmin() && userId != loggedInUserId) {
      log.error("Attempt to access different user id's {} reminders by user id {} ", userId, loggedInUserId);
      return new LinkedList<>();
    }

    return medicalRecordRepository.findBySearchItems(
            medicalRecordSearchFilter.getId(),
            medicalRecordSearchFilter.getPetId(),
            medicalRecordSearchFilter.getUserId(),
            medicalRecordSearchFilter.getMedicalRecordTypeId(),
            100,
            medicalRecordSearchFilter.getOffset());
  }

  public boolean deleteMedicalRecord(long id) {
    Long loggedInUserId = userService.getLoggedInUserId();
    boolean isUserAdmin = userService.isLoggedInUserAdmin();
    boolean isMedicalRecordIdPassedValid = id > 0;
    MedicalRecord medicalRecord = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
    if (!isUserAdmin && isMedicalRecordIdPassedValid && !medicalRecord.getUser().getId().equals(loggedInUserId)) {
      throw new AccessDeniedException("User is not authorised to change other user's Medical Record");
    }
    medicalRecord.setStatus(Status.DELETED);
    medicalRecordRepository.save(medicalRecord);
    return true;
  }

  public MedicalRecord storeMedicalRecord(MedicalRecord medicalRecordRequest) throws IOException {
    Long loggedInUserId = userService.getLoggedInUserId();
    FileUpload fileUpload = null;
    if (!StringUtils.isEmpty(medicalRecordRequest.getImageBytes())) {
       Optional<FileUpload> optionalFileUpload = s3service.getUrlAfterPhotoUpload(medicalRecordRequest.getImageBytes(), medicalRecordRequest.getImageFormat(), medicalRecordRequest.getImageName());
      if (optionalFileUpload.isEmpty()) throw new RuntimeException("Something went wrong while uploading the image, please try again after some time");
      fileUpload = optionalFileUpload.get();
    }
    User user = userRepository.findById(loggedInUserId).orElseThrow(() -> new BadRequestException("User not registered with us"));
    if (medicalRecordRequest.getUser().getId() != null && !user.getId().equals(medicalRecordRequest.getUser().getId())) {
      throw new AccessDeniedException();
    }
    MedicalRecord medicalRecord = medicalRecordRequest;
    if (medicalRecordRequest.getId() != null) {
      Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findById(medicalRecordRequest.getId());
      if (optionalMedicalRecord.isPresent()) medicalRecord = optionalMedicalRecord.get();
    }

    if (medicalRecord == null)
      throw new SplootException("No Medical Record exists by passed Id - " + medicalRecordRequest.getId(), HttpStatus.BAD_REQUEST);

    Long petId = medicalRecordRequest.getPetProfile().getId();
    PetProfile petProfile = petProfileRepository.findById(petId)
        .orElseThrow(() -> new BadRequestException("No Pet exists by passed Id - " + medicalRecordRequest.getPetProfile().getId()));

    if (Objects.nonNull(petProfile) && !petProfile.getUser().getId().equals(loggedInUserId))
      throw new SplootException("Non Admin user can not add medical record for some other user's pet - " + petId, HttpStatus.BAD_REQUEST);

    Long medicalRecordTypeId = (medicalRecordRequest.getMedicalRecordType() != null ? medicalRecordRequest.getMedicalRecordType().getId() : null);
    if (medicalRecordTypeId == null) throw new SplootException("invalid medical report type id", HttpStatus.BAD_REQUEST);
    MedicalRecordType medicalRecordType =  medicalRecordTypeRepository.findById(medicalRecordRequest.getMedicalRecordType().getId()).
        orElseThrow(() -> new SplootException("invalid medical report type id", HttpStatus.BAD_REQUEST));

    medicalRecord.setText(StringUtils.isNotBlank(medicalRecordRequest.getText()) ? medicalRecordRequest.getText():  medicalRecord.getText());
    medicalRecord.setPetProfile((petProfile == null) ? medicalRecord.getPetProfile() : petProfile);
    medicalRecord.setUser(user);
    medicalRecord.setImageUrl(StringUtils.isNotBlank(fileUpload.getUrl()) ? fileUpload.getUrl() : medicalRecord.getImageUrl());
    medicalRecord.setMedicalRecordType((medicalRecordType == null) ? medicalRecord.getMedicalRecordType() : medicalRecordType);
    medicalRecord.setStatus((medicalRecord.getStatus() == null) ? Status.ACTIVE : medicalRecord.getStatus());
    medicalRecord.setDateOfRecord(StringUtils.isNotBlank(medicalRecordRequest.getDateOfRecord()) ? medicalRecordRequest.getDateOfRecord() : medicalRecord.getDateOfRecord());
    return medicalRecordRepository.save(medicalRecord);

  }
}
