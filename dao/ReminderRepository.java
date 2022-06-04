package com.sploot.api.dao;

import com.sploot.api.model.entity.Reminder;
import com.sploot.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

  @Modifying
  @Transactional
  @Query(value = "update reminder r set status = 2 where parent_reminder_id = :parentReminderId and time > :reminderTime", nativeQuery = true)
  void deleteByParentReminderIdAndTimeAfter(@Param("parentReminderId") Long parentReminderId, @Param("reminderTime") long reminderTime);

  @Query(value = "select * from reminder tr where " +
      " (:reminderId is null or id = :reminderId) and " +
      " (:petId is null or user_pet_id = :petId) and " +
      " (:reminderTypeId is null or reminder_type_id = :reminderTypeId) and " +
      " (:medicalRecordTypeId is null or medical_record_type_id = :medicalRecordTypeId) and " +
      " (:userId is null or user_id = :userId) and " +
      " (:timeAfter is null or time >= :timeAfter) and " +
      " ((:completed is true or completed = :completed) or (:completed is false and completed = :completed))  and " +
      " (:timeBefore is null or time <= :timeBefore) and status = 0 " +
      " LIMIT :offset, :maxResult", nativeQuery = true)
  List<Reminder> findBySearchItems(@Param("reminderId") Long reminderId,
                                   @Param("medicalRecordTypeId") Long medicalRecordTypeId,
                                   @Param("petId") Long petId,
                                   @Param("reminderTypeId") Long reminderTypeId,
                                   @Param("userId") Long userId,
                                   @Param("timeAfter") Long timeAfter,
                                   @Param("timeBefore") Long timeBefore,
                                   @Param("completed") boolean completed,
                                   @Param("maxResult") Integer max,
                                   @Param("offset") Integer offset);

  @Query(value = "select * from reminder tr where " +
      " (:reminderId is null or id = :reminderId) and " +
      " (:petId is null or user_pet_id = :petId) and " +
      " ((reminder_type_id is null and medical_record_type_id = :medicalRecordTypeId) or " +
      " (medical_record_type_id is null and reminder_type_id = :reminderTypeId)) and " +
      " (:userId is null or user_id = :userId) and " +
      " (:timeAfter is null or time >= :timeAfter) and " +
      " ((:completed is true or completed = :completed) or (:completed is false and completed = :completed))  and " +
      " (:timeBefore is null or time <= :timeBefore) and status = 0 " +
      " LIMIT :offset, :maxResult", nativeQuery = true)
  List<Reminder> findBySearchItemsNonNullTypeId(@Param("reminderId") Long reminderId,
                                                @Param("medicalRecordTypeId") Long medicalRecordTypeId,
                                                @Param("petId") Long petId,
                                                @Param("reminderTypeId") Long reminderTypeId,
                                                @Param("userId") Long userId,
                                                @Param("timeAfter") Long timeAfter,
                                                @Param("timeBefore") Long timeBefore,
                                                @Param("completed") boolean completed,
                                                @Param("maxResult") Integer max,
                                                @Param("offset") Integer offset);


  List<Reminder> findByParentReminderIdAndDateCreatedGreaterThan(Long parentId, long currentDate);


  List<Reminder> findByParentReminderIdAndTimeAfter(Long parentId, long currentDate);

  @Modifying
  @Transactional
  void deleteAllByParentReminderIdAndDateCreatedGreaterThan(Long parentId, long currentDate);

  List<Reminder> findByUser(User user);

  @Query(value = "select tr.*, tsr.time from reminder tr join scheduled_reminder tsr on tr.id = tsr.reminder_id where " +
          " (:reminderId is null or tr.id = :reminderId) and " +
          " (:petId is null or tr.user_pet_id = :petId) and " +
          " (:reminderTypeId is null or tr.reminder_type_id = :reminderTypeId) and " +
          " (:medicalRecordTypeId is null or tr.medical_record_type_id = :medicalRecordTypeId) and " +
          " (:userId is null or tr.user_id = :userId) and " +
          " (:timeAfter is null or tsr.time >= :timeAfter) and " +
          " ((:completed is true or tsr.completed = :completed) or (:completed is false and tsr.completed = :completed))  and " +
          " (:timeBefore is null or tsr.time <= :timeBefore) and tr.status = 0" +
          " LIMIT :offset, :maxResult", nativeQuery = true)
  List<Reminder> getReminders(
          @Param("reminderId") Long reminderId,
          @Param("medicalRecordTypeId") Long medicalRecordTypeId,
          @Param("petId") Long petId,
          @Param("reminderTypeId") Long reminderTypeId,
          @Param("userId") Long userId,
          @Param("timeAfter") Long timeAfter,
          @Param("timeBefore") Long timeBefore,
          @Param("completed") boolean completed,
          @Param("maxResult") Integer max,
          @Param("offset") Integer offset
  );
}
