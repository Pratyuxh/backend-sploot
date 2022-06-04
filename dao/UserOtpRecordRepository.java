package com.sploot.api.dao;

import com.sploot.api.constant.enums.OtpType;
import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.User;
import com.sploot.api.model.entity.UserOtpRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOtpRecordRepository extends CommonRepositoryInterface<UserOtpRecord, Long> {

//	UserOtpRecord findByUser(User user);

//	List<UserOtpRecord> findByUserOrderByLastUpdatedDesc(User user);

	UserOtpRecord findByUserAndOtpType(User user, OtpType otpType);

	UserOtpRecord findByUserAndStatus(User user, Status inactive);
}
