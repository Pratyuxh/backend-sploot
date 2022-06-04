package com.sploot.api.dao;

import com.sploot.api.model.entity.ScheduledReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledReminderRepository extends JpaRepository<ScheduledReminder, Long> {

    @Query(value = "update scheduled_reminder set status = 2 where reminder_id = :reminderId", nativeQuery = true)
    @Modifying
    void deleteByReminderId(@Param("reminderId") Long reminderId);
}
