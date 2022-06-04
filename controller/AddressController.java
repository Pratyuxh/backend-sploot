package com.sploot.api.controller;


import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.City;
import com.sploot.api.model.entity.Country;
import com.sploot.api.model.entity.State;
import com.sploot.api.model.mapper.CityMapper;
import com.sploot.api.model.mapper.CountryMapper;
import com.sploot.api.model.mapper.StateMapper;
import com.sploot.api.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AddressController {

	@Autowired
	private AddressService addressService;

	@Autowired
	private CountryMapper countryMapper;

	@Autowired
	private StateMapper stateMapper;

	@Autowired
	private CityMapper cityMapper;

	@GetMapping("/v1/getCountryList")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto<?> getCountryList() {
		log.debug("Start of method getCountryList");
		List<Country> countryList = addressService.getCountryList();
		if (countryList.isEmpty()) return new ErrorResponseDto<>(null, "No CountryList found in data store");
		List<CountryDTO> countryDTOList = countryList.stream().map(country -> countryMapper.entityToResponseDTO(country)).collect(Collectors.toList());
		log.debug("End of method getCountryList, return value: {}", countryDTOList);
		return new SuccessResponseDto<>(countryList, "Success in fetching CountryList ");
	}

	@GetMapping("/v1/getStateList")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto<?> getStateList(@RequestParam String countryName) {
		log.debug("Start of method getStateList, param: CountryName: {}", countryName);
		List<State> stateList = addressService.getStateList(countryName);
		if (stateList.isEmpty()) return new ErrorResponseDto<>(null, "No StateList found in data store");
		List<StateDTO> stateDTOList = stateList.stream().map(state -> stateMapper.entityToResponseDTO(state)).collect(Collectors.toList());
		log.debug("End of method getStateList, return value: {}", stateDTOList);
		return new SuccessResponseDto<>(stateDTOList, "Success in fetching StateList ");
	}

	@GetMapping("/v1/getCityList")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto<?> getCityList(@RequestParam(required = false) String  stateName) {
		log.debug("Start of method getCityList, param: StateName: {}", stateName);
		List<City> cityList = addressService.getCityList(stateName);
		if (cityList.isEmpty()) return new ErrorResponseDto<>(null, "No CityList found in data store");
		List<CityDTO> cityDTOList = cityList.stream().map(city -> cityMapper.entityToResponseDTO(city)).collect(Collectors.toList());
		log.debug("End of method getCityList, return value: {}", cityDTOList);
		return new SuccessResponseDto<>(cityList, "Success in fetching CityList ");
	}
}
