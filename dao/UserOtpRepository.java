package com.sploot.api.dao;

import com.sploot.api.model.entity.UserOtpEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserOtpRepository extends CommonRepositoryInterface<UserOtpEntity, Long>{

  @Query(value = "select * from user_otp uo where uo.mobile_no  = :mobileNo order by id desc limit 1", nativeQuery = true)
  Optional<UserOtpEntity> findOneByMobileNo(@Param("mobileNo") String mobileNo);

}
