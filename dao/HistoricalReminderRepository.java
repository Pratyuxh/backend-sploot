package com.sploot.api.dao;

import com.sploot.api.model.entity.HistoricalReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricalReminderRepository extends JpaRepository<HistoricalReminder, Long> {

  // status 2 is for delete (soft delete)
  @Modifying
  @Transactional
  @Query(value = "update historical_reminder r set status = 2 where parent_reminder_id = :parentReminderId and time > :reminderTime", nativeQuery = true)
  void deleteByParentReminderIdAndTimeAfter(@Param("parentReminderId") Long parentReminderId, @Param("reminderTime") long reminderTime);

  Optional<HistoricalReminder> findByPurgedReminderId(long id);

  @Query(value = "select * from historical_reminder tr where " +
      " (:reminderId is null or id = :reminderId) and " +
      " (:petId is null or user_pet_id = :petId) and " +
      " (:reminderTypeId is null or reminder_type_id = :reminderTypeId) and " +
      " (:medicalRecordTypeId is null or medical_record_type_id = :medicalRecordTypeId) and " +
      " (:userId is null or user_id = :userId) and " +
      " (:timeAfter is null or time >= :timeAfter) and " +
      " ((:completed is true or completed = :completed) or (:completed is false and completed = :completed))  and " +
      " (:timeBefore is null or time <= :timeBefore) and status = 0 " +
      " LIMIT :offset, :maxResult", nativeQuery = true)
  List<HistoricalReminder> findBySearchItems(@Param("reminderId") Long reminderId,
      @Param("petId") Long petId,
      @Param("reminderTypeId") Long reminderTypeId,
      @Param("medicalRecordTypeId") Long medicalRecordTypeId,
      @Param("userId") Long userId,
      @Param("timeAfter") Long timeAfter,
      @Param("timeBefore") Long timeBefore,
      @Param("completed") boolean completed,
      @Param("maxResult") Integer max,
      @Param("offset") Integer offset);

  @Query(value = "select * from historical_reminder tr where " +
      " (:reminderId is null or id = :reminderId) and " +
      " (:petId is null or user_pet_id = :petId) and " +
      " ((reminder_type_id is null and medical_record_type_id = :medicalRecordTypeId) or " +
      " (medical_record_type_id is null and reminder_type_id = :reminderTypeId)) and " +
      " (:userId is null or user_id = :userId) and " +
      " (:timeAfter is null or time >= :timeAfter) and " +
      " ((:completed is true or completed = :completed) or (:completed is false and completed = :completed))  and " +
      " (:timeBefore is null or time <= :timeBefore) and status = 0 " +
      " LIMIT :offset, :maxResult", nativeQuery = true)
  List<HistoricalReminder> findBySearchItemsNonNullTypeId(@Param("reminderId") Long reminderId,
      @Param("petId") Long petId,
      @Param("reminderTypeId") Long reminderTypeId,
      @Param("medicalRecordTypeId") Long medicalRecordTypeId,
      @Param("userId") Long userId,
      @Param("timeAfter") Long timeAfter,
      @Param("timeBefore") Long timeBefore,
      @Param("completed") boolean completed,
      @Param("maxResult") Integer max,
      @Param("offset") Integer offset);

  HistoricalReminder findByIdOrPurgedReminderId(long id, long purgedReminderId);

}
