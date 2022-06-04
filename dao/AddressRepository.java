package com.sploot.api.dao;

import com.sploot.api.model.entity.Address;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CommonRepositoryInterface<Address, Long> {
}
