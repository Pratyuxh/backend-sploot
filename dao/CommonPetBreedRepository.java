package com.sploot.api.dao;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.CommonPetBreed;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonPetBreedRepository extends CommonRepositoryInterface<CommonPetBreed, Long> {
	List<CommonPetBreed> findByStatus(Status status);
}
