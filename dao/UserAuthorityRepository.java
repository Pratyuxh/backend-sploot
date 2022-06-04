package com.sploot.api.dao;

import com.sploot.api.model.entity.UserAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserAuthorityRepository extends CommonRepositoryInterface<UserAuthority, Long> {
	Page<UserAuthority> findAll(Pageable pageable);

	Set<UserAuthority> findByIdIn(List<Long> authoritiesIds);

	UserAuthority findByName(String adminRole);

//	List<UserAuthority> findByUser(User user);

//	@Query(value = "select * from authorities where username = :username", nativeQuery = true)
//	List<UserAuthority> findByUsername(@Param("username") String username);
}
