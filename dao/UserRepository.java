package com.sploot.api.dao;

import com.sploot.api.constant.enums.Status;
import com.sploot.api.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CommonRepositoryInterface<User, Long>{

	Boolean existsByEmail(String email);

	@Query(value = "select * from users ur where ur.email  = :username", nativeQuery = true)
	Optional<User> findByEmail(@Param("username") String username);

	@Query(value = "select * from users ur where ur.mobile_no  = :mobileNo", nativeQuery = true)
	Optional<User> findByMobileNo(@Param("mobileNo") String mobileNo);

	Page<User> findAll(Pageable pageable);

	@Query(value = "select * from users ur inner join authorities au on au.username=ur.username where au.authority='ROLE_USER' and ur.enabled=1", nativeQuery = true)
	List<User> findAllUsersByEnabled();

	@Query(value = "select * from users ur where ur.referral_code = :refer_code and ur.enabled=1", nativeQuery = true)
    User findUserByPromocode(@Param("refer_code") String referCode);

	List<User> findByFirstName(String firstName);

//	List<User> findByReferralCode(String refCode);

	@Query(value = "select * from users ur where ur.username like :passedEmail", nativeQuery = true)
	Set<User> findByEmailLike(@Param("passedEmail") String passedEmail);

	@Query(value = "select * from users ur where ur.username like :passedEmail and created_by = :created_by", nativeQuery = true)
	Set<User> getByEmailLikeAndCreatedBy(@Param("passedEmail") String passedEmail, @Param("created_by") Long id);


	@Query("SELECT DISTINCT user FROM User user " +
			"INNER JOIN FETCH user.userAuthorities AS userAuthorities " +
			"WHERE user.username = :username")
    User findByUsername(@Param("username") String username);

	List<User> findByUsernameLike(String usernameStr);

//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})
	@Query(value = "select * from users ur where ur.username like :username or ur.email = :username", nativeQuery = true)
    User findByUsernameOrEmail(@Param("username") String username);

	List<User> findByLastLoggedInLessThan(long milliseconds);
	List<User> findByStatus(Status status);

    List<User> findByEmailIn(List<String> usersEmails);

	/*

	copied from registered users
		@Query(value = "select * from registered_users ur where ur.email  = :username", nativeQuery = true)
	List<User> findByEmail(@Param("username") String username);

	Page<User> findAll(Pageable pageable);

	@Query(value = "select * from registered_users ur inner join authorities au on au.username=ur.username where au.authority='ROLE_USER' and ur.enabled=1", nativeQuery = true)
	List<User> findAllUsersByEnabled();

	@Query(value = "select * from registered_users ur where ur.referral_code = :refer_code and ur.enabled=1", nativeQuery = true)
	User findUserByPromocode(@Param("refer_code") String referCode);

	List<User> findByFirstName(String firstName);

//	List<User> findByReferralCode(String refCode);

	@Query(value = "select * from registered_users ur where ur.username like :passedEmail", nativeQuery = true)
	Set<User> findByEmailLike(@Param("passedEmail") String passedEmail);

	@Query(value = "select * from registered_users ur where ur.username like :passedEmail and created_by = :created_by", nativeQuery = true)
	Set<User> getByEmailLikeAndCreatedBy(@Param("passedEmail") String passedEmail, @Param("created_by") Long id);


	@Query("SELECT DISTINCT user FROM User user " +
			"INNER JOIN FETCH user.userAuthorities AS userAuthorities " +
			"WHERE user.username = :username")
	User findByUsername(@Param("username") String username);

	List<User> findByUsernameLike(String usernameStr);

//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})
	@Query(value = "select * from registered_users ur where ur.username like :username or ur.email = :username", nativeQuery = true)
	User findByUsernameOrEmail(@Param("username") String username);

	List<User> findByLastLoggedInLessThan(long milliseconds);
	List<User> findByStatus(Status status);

    List<User> findByEmailIn(List<String> usersEmails);








	 */

}
