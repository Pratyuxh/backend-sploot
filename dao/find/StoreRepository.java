package com.sploot.api.dao.find;

import com.sploot.api.model.entity.find.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query(value = "SELECT * FROM find_store s,find_store_address address ,cities cities  WHERE s.address_id = address.id  and cities.id=address.city_id and  cities.name = :name", nativeQuery = true)
    List<Store> fetchByCityName(@Param("name") String name);



}
