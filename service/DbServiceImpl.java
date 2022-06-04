package com.sploot.api.service;

import com.sploot.api.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DbServiceImpl implements DbService {
	@Autowired
	S3service s3Service;
	@Autowired
	ReminderRepository reminderRepository;
	@Autowired
	HistoricalReminderRepository historicalReminderRepository;
	@Autowired
	PetProfileRepository petProfileRepository;
	@Autowired
	ReminderTypeRepository reminderTypeRepository;
	@Autowired
	MedicalRecordRepository medicalRecordRepository;
	@Autowired
	MedicalRecordTypeRepository medicalRecordTypeRepository;
	@Autowired
	UserRepository userRepository;

//	@Override
//	public Map<String, List<ReminderTypeDTO>> findActiveReminderTypes() {
//		return null;
//		// TODO kapil mapper correct this

//		Type listType = new TypeToken<List<ReminderTypeDTO>>() {
//		}.getType();
//		List<ReminderType> reminderTypeList = reminderTypeRepository.findByStatus(Status.ACTIVE);
//		List<ReminderTypeDTO> reminderTypeDTOS = (CollectionUtils.isEmpty(reminderTypeList)) ? new LinkedList<>() :
//				modelMapper.map(reminderTypeList, listType);
//		return reminderTypeDTOS.stream().collect(Collectors.groupingBy(reminderTypeDto ->
//				reminderTypeDto.getReminderCategory().getDescription(), Collectors.toList()));
//	}

//	@Override
//	public List<ReminderDto> findActiveRemindersForUser(ReminderSearchFilter reminderSearchFilter) {
//		MedicalRecordType medicalRecordType = (reminderSearchFilter.getReminderTypeId() != null && reminderSearchFilter.getReminderTypeId() > 0) ?
//				findMatchingMedicalRecordType(reminderSearchFilter.getReminderTypeId()) : null;
//		ReminderType reminderType = (reminderSearchFilter.getMedicalRecordTypeId() != null && reminderSearchFilter.getMedicalRecordTypeId() > 0) ?
//				findMatchingReminderType(reminderSearchFilter.getMedicalRecordTypeId()) : null;
//		if (reminderType != null) reminderSearchFilter.setReminderTypeId(reminderType.getId());
//		if (medicalRecordType != null) reminderSearchFilter.setMedicalRecordTypeId(medicalRecordType.getId());
//		List<ReminderDto> reminders = new LinkedList<>();//medicalRecordType != null) ?
////				getMedicalSameTypeReminders(medicalRecordType, reminderSearchFilter) : new LinkedList<>();
//		setMaxForReminderSearchFilter(reminderSearchFilter);
//		long currentTime = System.currentTimeMillis();
//		if (reminderSearchFilter.getTimeBefore() != null && reminderSearchFilter.getTimeAfter() != null) {
//			if (reminderSearchFilter.getTimeBefore() < reminderSearchFilter.getTimeAfter()) {
//				String msg = "Start time can not be greater than end time. ";
//				log.error(msg);
//				return new LinkedList<>();
//			}
//			boolean isOnlyCurrentAndFutureReminders = reminderSearchFilter.getTimeAfter() >= currentTime;
//			boolean isOnlyHistoricalReminders = reminderSearchFilter.getTimeBefore() <= currentTime;
//			if (isOnlyCurrentAndFutureReminders) {
//				log.info("Only current and future reminders are requested : between {} and {}",
//						DateTimeUtils.getDateFromMilliseconds(reminderSearchFilter.getTimeAfter()),
//						DateTimeUtils.getDateFromMilliseconds(reminderSearchFilter.getTimeBefore()));
//				reminders.addAll(findCurrentFutureRemindersForUser(reminderSearchFilter));
//				return reminders;
//			}
//			if (isOnlyHistoricalReminders) {
//				log.info("Only historical reminders are requested : between {} and {}",
//						DateTimeUtils.getDateFromMilliseconds(reminderSearchFilter.getTimeAfter()),
//						DateTimeUtils.getDateFromMilliseconds(reminderSearchFilter.getTimeBefore()));
//				HistoricalReminderSearchFilter historicalReminderSearchFilter = null;
//				// TODO kapil mapper correct this
//				//modelMapper.map(reminderSearchFilter, HistoricalReminderSearchFilter.class);
//				reminders.addAll(findHistoricalRemindersForUser(historicalReminderSearchFilter));
//				return reminders;
//			}
//		}
//		boolean isMixOfPastAndPresentAndFutureReminders = reminderSearchFilter.getTimeAfter() != null &&
//				reminderSearchFilter.getTimeAfter() < currentTime &&
//				(reminderSearchFilter.getTimeBefore() == null || reminderSearchFilter.getTimeBefore() >= currentTime);
//		if (isMixOfPastAndPresentAndFutureReminders) {
//			HistoricalReminderSearchFilter historicalReminderSearchFilter = null;
//			// TODO kapil mapper correct this
//			//modelMapper.map(reminderSearchFilter, HistoricalReminderSearchFilter.class);
////			reminderSearchFilter.setTimeAfter(currentTime);
//			historicalReminderSearchFilter.setTimeBefore(currentTime);
//			reminders.addAll(new LinkedList<ReminderDto>() {{
//				addAll(findHistoricalRemindersForUser(historicalReminderSearchFilter));
//				addAll(findCurrentFutureRemindersForUser(reminderSearchFilter));
//			}});
//			return reminders;
//		}
//		log.warn("Not time interval condition is matching... Going by basic search");
//		return findCurrentFutureRemindersForUser(reminderSearchFilter);
//	}
//
//	private ReminderType findMatchingReminderType(Long medicalRecordTypeId) {
//
//		MedicalRecordType reminderType = medicalRecordTypeRepository.findById(medicalRecordTypeId).orElse(null);
//		if (reminderType == null) {
//			log.warn("Reminder type not found by passed Id : {}", medicalRecordTypeId);
//			return null;
//		}
//		Optional<ReminderType> reminderTypeOptional = reminderTypeRepository.findByNameIgnoreCase(reminderType.getName());
//		return reminderTypeOptional.isPresent() ? reminderTypeOptional.get() : null;
//	}
//
//	private List<ReminderDto> getMedicalSameTypeReminders(MedicalRecordType medicalRecordType, ReminderSearchFilter reminderSearchFilter) {
//		long reminderTypeId = reminderSearchFilter.getReminderTypeId();
//		reminderSearchFilter.setReminderTypeId(null);
//		reminderSearchFilter.setMedicalRecordTypeId(medicalRecordType.getId());
//		List<ReminderDto> reminderDtos = findActiveRemindersForUser(reminderSearchFilter);
//		reminderSearchFilter.setReminderTypeId(reminderTypeId);
//		reminderSearchFilter.setMedicalRecordTypeId(null);
//		return reminderDtos;
//	}
//
//	private MedicalRecordType findMatchingMedicalRecordType(Long reminderTypeId) {
//		ReminderType reminderType = reminderTypeRepository.findById(reminderTypeId).orElse(null);
//		if (reminderType == null) {
//			log.warn("Reminder type not found by passed Id : {}", reminderTypeId);
//			return null;
//		}
//		return medicalRecordTypeRepository.findByNameIgnoreCase(reminderType.getName());
//	}

//	private void setMaxForReminderSearchFilter(ReminderSearchFilter reminderSearchFilter) {
//		if (reminderSearchFilter.getGroupByDate() == null || reminderSearchFilter.getGroupByDate())
//			reminderSearchFilter.setMax(Integer.MAX_VALUE);
//	}
//
//	@Override
//	public List<ReminderDto> findCurrentFutureRemindersForUser(ReminderSearchFilter reminderSearchFilter) {
////		Type listType = new TypeToken<List<ReminderDto>>() {
////		}.getType();
//		if (reminderSearchFilter.getReminderTypeId() != null && reminderSearchFilter.getMedicalRecordTypeId() != null) {
//			List<Reminder> reminderList = reminderRepository.findBySearchItemsNonNullTypeId(
//					reminderSearchFilter.getReminderId(),
//					reminderSearchFilter.getMedicalRecordTypeId(),
//					reminderSearchFilter.getPetId(),
//					reminderSearchFilter.getReminderTypeId(),
//					reminderSearchFilter.getUserId(),
//					reminderSearchFilter.getTimeAfter(),
//					reminderSearchFilter.getTimeBefore(),
//					reminderSearchFilter.isIncludeCompleted(),
//					reminderSearchFilter.getMax(),
//					reminderSearchFilter.getOffset());
//			return null;
////			return (CollectionUtils.isEmpty(reminderList)) ? new LinkedList<>() : modelMapper.map(reminderList, listType);
//		} else {
//			List<Reminder> reminderList = reminderRepository.findBySearchItems(
//					reminderSearchFilter.getReminderId(),
//					reminderSearchFilter.getMedicalRecordTypeId(),
//					reminderSearchFilter.getPetId(),
//					reminderSearchFilter.getReminderTypeId(),
//					reminderSearchFilter.getUserId(),
//					reminderSearchFilter.getTimeAfter(),
//					reminderSearchFilter.getTimeBefore(),
//					reminderSearchFilter.isIncludeCompleted(),
//					reminderSearchFilter.getMax(),
//					reminderSearchFilter.getOffset());
//			return null;
////			return (CollectionUtils.isEmpty(reminderList)) ? new LinkedList<>() : modelMapper.map(reminderList, listType);
//		}
//	}
//
//	@Override
//	public List<ReminderDto> findHistoricalRemindersForUser(HistoricalReminderSearchFilter historicalReminderSearchFilter) {
////		Type listType = new TypeToken<List<ReminderDto>>() {
////		}.getType();
//		if (historicalReminderSearchFilter.getReminderTypeId() != null && historicalReminderSearchFilter.getMedicalRecordTypeId() != null) {
//
//			List<HistoricalReminder> reminderList = historicalReminderRepository.findBySearchItemsNonNullTypeId(
//					historicalReminderSearchFilter.getReminderId(),
//					historicalReminderSearchFilter.getPetId(),
//					historicalReminderSearchFilter.getReminderTypeId(),
//					historicalReminderSearchFilter.getMedicalRecordTypeId(),
//					historicalReminderSearchFilter.getUserId(),
//					historicalReminderSearchFilter.getTimeAfter(),
//					historicalReminderSearchFilter.getTimeBefore(),
//					historicalReminderSearchFilter.isIncludeCompleted(),
//					historicalReminderSearchFilter.getMax(),
//					historicalReminderSearchFilter.getOffset());
//			return null;
////			return (CollectionUtils.isEmpty(reminderList)) ? new LinkedList<>() : modelMapper.map(reminderList, listType);
//		} else {
//			List<HistoricalReminder> reminderList = historicalReminderRepository.findBySearchItems(
//					historicalReminderSearchFilter.getReminderId(),
//					historicalReminderSearchFilter.getPetId(),
//					historicalReminderSearchFilter.getReminderTypeId(),
//					historicalReminderSearchFilter.getMedicalRecordTypeId(),
//					historicalReminderSearchFilter.getUserId(),
//					historicalReminderSearchFilter.getTimeAfter(),
//					historicalReminderSearchFilter.getTimeBefore(),
//					historicalReminderSearchFilter.isIncludeCompleted(),
//					historicalReminderSearchFilter.getMax(),
//					historicalReminderSearchFilter.getOffset());
//			return null;
////			return (CollectionUtils.isEmpty(reminderList)) ? new LinkedList<>() : modelMapper.map(reminderList, listType);
//		}
//	}
//
//	@Override
//	public List<MedicalRecordTypeDTO> findActiveMedicalRecordTypes(Long userId, Long petId) {
//		List<MedicalRecordTypeDTO> medicalRecordTypeDTOS =this.findActiveMedicalRecordTypes();
//		medicalRecordTypeDTOS.forEach(medicalRecordTypeDto -> updateWithRecentEntry(userId, medicalRecordTypeDto, petId));
//		return medicalRecordTypeDTOS;
//	}
//	@Override
//	public List<MedicalRecordTypeDTO> findActiveMedicalRecordTypes() {
////		Type listType = new TypeToken<List<MedicalRecordTypeDto>>() {
////		}.getType();
//		List<MedicalRecordType> medicalRecordTypeList = medicalRecordTypeRepository.findByStatus(Status.ACTIVE);
//		return null;
////		List<MedicalRecordTypeDto> medicalRecordTypeDtos = (CollectionUtils.isEmpty(medicalRecordTypeList)) ? new LinkedList<>() : modelMapper.map(medicalRecordTypeList, listType);
////		return medicalRecordTypeDtos;
//	}
//
//	private void updateWithRecentEntry(Long userId, MedicalRecordTypeDTO medicalRecordTypeDto, Long petId) {
//		MedicalRecord medicalRecord = medicalRecordRepository.findOneByUserAndMedicalRecordTypeAndStatus(userId, medicalRecordTypeDto.getId(), Status.ACTIVE.ordinal());
//		if (medicalRecord != null && ((petId == null) || medicalRecord.getPetProfile().getId().equals(petId))) {
//			medicalRecordTypeDto.setRecentEntry(medicalRecord.getText());
//			medicalRecordTypeDto.setCreationTime(medicalRecord.getDateCreated());
//		}
//	}
//
//	@Override
//	public List<MedicalRecordResponseDTO> findActiveMedicalRecordsForUser(MedicalRecordSearchFilter medicalRecordSearchFilter) {
////		Type listType = new TypeToken<List<MedicalRecordDto>>() {
////		}.getType();
//		List<MedicalRecord> reminderList = medicalRecordRepository.findBySearchItems(medicalRecordSearchFilter.getId(), medicalRecordSearchFilter.getPetId(),
//				medicalRecordSearchFilter.getUserId(),
//				medicalRecordSearchFilter.getMedicalRecordTypeId(),
//				100,// currently giving 100 most recent images.
////				medicalRecordSearchFilter.getMax(),
//				medicalRecordSearchFilter.getOffset());
//		return null;
////		return (CollectionUtils.isEmpty(reminderList)) ? new LinkedList<>() : modelMapper.map(reminderList, listType);
//	}
//
//	@Override
//	public List<User> getInactiveUsersSinceNumDays(Integer numInactivityDays) {
//		return userRepository.findByLastLoggedInLessThan(DateTimeUtils.get0000HrsMilliseconds(DateTimeUtils.getDateOnlyAheadOrBehind(numInactivityDays, false)));
//	}
//
//	@Override
//	public List<PetProfile> getIncompletePetProfiles(Integer numInactivityDays) {
//		return petProfileRepository.findIncompletePetProfiles();
//	}

//	@Override
//	public boolean DateSeparatedReminderDto(String cachekey) {
//		try {
//			redisTemplate.opsForHash().delete(cachekey, ApplicationConstant.OBJECT_HASH_KEY);// logic in storing in hashmap currently is a little unconventional
//			return true;
//		} catch (Exception e) {
//			log.error("Error in deleting key {}", cachekey, e);
//			return false;
//		}
//	}
//
//
//
//	public String deleteFileFromS3Bucket(String fileUrl) {
//		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//		s3Service.deleteFile(fileName);
//		return "Successfully deleted";
//	}

}
