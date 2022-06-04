package com.sploot.api.dao;

import com.sploot.api.model.entity.CommonDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface CommonRepositoryInterface<T extends CommonDataEntity, T1> extends JpaRepository<T, T1> {
	List<T> findByDateCreatedBetween(Date startDate, Date endDate);
}
