package com.sploot.api.dao;


import com.sploot.api.model.entity.PushNotification;
import com.sploot.api.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {

//  @Query(value = "select * from posts where FIND_IN_SET(:search, tags)", nativeQuery = true)
//  List<PostEntity> getByTags(@Param("search") String search);

  List<PushNotification> findAllByUserAndFirebaseID(User user, String firebaseID, Pageable page);
  List<PushNotification> findByUser(User user);
}
