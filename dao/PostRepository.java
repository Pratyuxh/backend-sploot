package com.sploot.api.dao;

import com.sploot.api.model.entity.Post;
import com.sploot.api.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  @Query(value = "select * from posts where FIND_IN_SET(:search, tags)", nativeQuery = true)
  List<Post> getByTags(@Param("search") String search);

  List<Post> findAllByUser(User user, Pageable pageable);

  List<Post> findAllByIsMemoryAndUser(Boolean isMemory, Pageable pageable, User user);

  List<Post> findAllByUser(User user);

  Page<Post> findAll(Pageable paging);

  @Override
  List<Post> findAllById(Iterable<Long> longs);

  List<Post> findByCreatedAtBetween(Timestamp fromTimestamp, Timestamp toTimestamp);
}
