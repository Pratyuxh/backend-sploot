package com.sploot.api.dao;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.MedicalRecordType;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordTypeRepository extends JpaRepository<MedicalRecordType, Long> {

  List<MedicalRecordType> findByStatus(Status status);
  MedicalRecordType findByNameIgnoreCase(String name);

}
