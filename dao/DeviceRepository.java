package com.sploot.api.dao;

import com.sploot.api.model.entity.Device;
import com.sploot.api.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeviceRepository extends CommonRepositoryInterface<Device, Long> {
	Device findFirstByUserAndFireBaseId(User user, String fireBaseId);

	Device findByFireBaseId(String fireBaseId);

	List<Device> findByUser(User user);

	List<Device> findByUserIn(List<User> inactiveUsers);

	Device findByFireBaseIdAndUser(String fireBaseId, User user);

	@Query(value = "select * from user_devices where user_id = :userID order by date_updated limit 1", nativeQuery = true)
	Device findLatestDeviceOfAUser(@Param("userID") Long userID);
}

