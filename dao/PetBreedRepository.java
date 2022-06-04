package com.sploot.api.dao;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.PetBreed;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetBreedRepository extends CommonRepositoryInterface<PetBreed, Long> {
	List<PetBreed> findByStatus(Status status);
}
