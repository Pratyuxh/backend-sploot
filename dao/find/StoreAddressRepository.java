package com.sploot.api.dao.find;

import com.sploot.api.model.entity.find.StoreAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StoreAddressRepository extends JpaRepository<StoreAddress, Long> {

}