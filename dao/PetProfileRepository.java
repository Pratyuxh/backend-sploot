package com.sploot.api.dao;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.PetProfile;
import com.sploot.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetProfileRepository extends JpaRepository<PetProfile, Long> {

  Optional<PetProfile> findById(Long petID);

  List<PetProfile> findByUserId(Long userId);

//  @Query(value = "select * from pet_profile where pet_user_id = :petUserId and status = 0", nativeQuery = true)
//  List<PetProfile> findByUserId(@Param("petUserId") Long loggedInUserId);

  PetProfile findByUserAndIsDefault(User user, boolean isDefault);
  PetProfile findByUserAndIsDefaultAndStatus(User user, boolean isDefault, Status status);

  @Query(value = "select * from pet_profile where (gender is null or name is null or profile_pic_url is null or date_of_birth is null) and status = 0", nativeQuery = true)
  List<PetProfile> findIncompletePetProfiles();


}
