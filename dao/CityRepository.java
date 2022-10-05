package com.sploot.api.dao;

import com.sploot.api.model.entity.City;
import com.sploot.api.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CityRepository extends JpaRepository<City, Long> {

    City findByNameAndStateOrderByNameAsc(String cityPassed, State state);
    List<City> findByStateOrderByNameAsc(State state);
}
