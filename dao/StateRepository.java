package com.sploot.api.dao;

import com.sploot.api.model.entity.Country;
import com.sploot.api.model.entity.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface StateRepository extends CrudRepository<State, Long> {

	State findByName(String state);

	State findByNameAndCountryOrderByCountryAsc(String statePassed, Country country);

	List<State> findByCountryOrderByNameAsc(Country id);

}
