package com.sploot.api.dao;

import com.sploot.api.model.entity.MedicalRecord;
import com.sploot.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

  @Query(value = "select * from medical_record tr where " +
      " (:recordId is null or id = :recordId) and " +
      " (:petId is null or pet_profile_id = :petId) and " +
      " (:medicalRecordTypeId is null or medical_record_type_id = :medicalRecordTypeId) and " +
      " (:userId is null or user_id = :userId) and " +
      " status = 0 order by id desc " +
      " LIMIT :offset, :max", nativeQuery = true)
  List<MedicalRecord> findBySearchItems(@Param("recordId") Long id,
      @Param("petId") Long petId,
      @Param("userId") Long userId,
      @Param("medicalRecordTypeId") Long medicalRecordTypeId,
      @Param("max") Integer max,
      @Param("offset") Integer offset);

  @Query(value = "select * from medical_record where user_id = :userId and medical_record_type_id = :medicalRecordTypeId order by date_created desc limit 1", nativeQuery = true)
  MedicalRecord findOneByUserAndMedicalRecordType(@Param("userId") Long userId, @Param("medicalRecordTypeId") Long id);

  @Query(value = "select * from medical_record where user_id = :userId and medical_record_type_id = :medicalRecordTypeId and status = :status order by date_created desc limit 1", nativeQuery = true)
  MedicalRecord findOneByUserAndMedicalRecordTypeAndStatus(@Param("userId") Long userId, @Param("medicalRecordTypeId") Long id, @Param("status") Integer status);

  List<MedicalRecord> findByUser(User user);

}
