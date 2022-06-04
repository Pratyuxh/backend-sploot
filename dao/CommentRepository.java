package com.sploot.api.dao;


import com.sploot.api.model.entity.CommentEntity;
import com.sploot.api.model.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findAllByPost(Pageable pageable, Post post);
  List<CommentEntity> findAllByPost(Post post);
  Optional<CommentEntity> findByIdAndThreadID(Long id, Integer threadID);

  @Query(value = "select * from comments cm where cm.post_id = :postid and cm.thread_id = :threadid", nativeQuery = true)
  List<CommentEntity> findByThreadIDAndPost(@Param("threadid") Integer threadid, @Param("postid") Integer postid);

  List<CommentEntity> findByCreatedAtBetween(Timestamp from, Timestamp to);
  List<CommentEntity> findByThreadID(Integer threadID);

}
