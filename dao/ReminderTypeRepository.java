package com.sploot.api.dao;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderTypeRepository extends JpaRepository<ReminderType, Long> {

  List<ReminderType> findByStatus(Status active);

  List<ReminderType> findByNameInIgnoreCase(List<String> medicalRecordTypesList);

  Optional<ReminderType> findByNameIgnoreCase(String medicalRecordTypes);


}
