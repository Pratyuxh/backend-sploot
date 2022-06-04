package com.sploot.api.dao;

import com.sploot.api.model.entity.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CountryRepository extends CrudRepository<Country, Long> {

	Country findByName(String country);

	List<Country> findAllByOrderByNameAsc();
}

