package com.sploot.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sploot.api.constant.enums.PostNotificationType;
import com.sploot.api.dao.DeviceRepository;
import com.sploot.api.dao.PushNotificationRepository;
import com.sploot.api.model.dto.CreatedBy;
import com.sploot.api.model.dto.PostNotificationDTO;
import com.sploot.api.model.dto.PushNotificationDTO;
import com.sploot.api.model.entity.*;
import com.sploot.api.util.FeedUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncNotificationService {

  @Autowired
  private DeviceRepository deviceRepository;


  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private PetService petService;

  @Autowired
  private PushNotificationRepository pushNotificationRepository;

  @Value("${firebase.notification.push.url}")
  private String firebaseUrl;

  @Value("${firebase.notification.push.token}")
  private String firebaseToken;


  @Async
  public void sendNotificationToPostCreater(PostNotificationType commentAdded,
                                            Post post, User user, String timestamp, Object data)
      throws JsonProcessingException {
    List<String> firebaseIds = getFirebaseIds(Collections.singleton(post.getUser()));
    if (firebaseIds.size() > 0) {
      sendPostPushNotification(commentAdded, post, user, timestamp, firebaseIds, data);
    }
  }

  @Async
  public void sendNotificationToPostFollower(PostNotificationType postFollowers,
      Post post,
      User user, String timestamp, Object data) throws JsonProcessingException {
    List<CommentEntity> listOfComment = post.getComment();
    Set<User> users = listOfComment.stream()
        .filter(cm -> !cm.getUser().getId().equals(post.getUser().getId())
            && !cm.getUser().getId().equals(user.getId())
            && cm.getUser().getId() != post.getUser().getId()
            && cm.getUser().getId() != user.getId())
        .map(cm -> cm.getUser())
        .collect(Collectors.toSet());
    List<String> firebaseIds = getFirebaseIds(users);
    if (firebaseIds.size() > 0) {
      sendPostPushNotification(postFollowers, post, user, timestamp, firebaseIds, data);
    }
  }

  private void sendPostPushNotification(PostNotificationType notType, Post post,
      User user, String timestamp, List<String> firebaseIds, Object data)
      throws JsonProcessingException {

    List<PetProfile> petProfiles = petService.getPetProfilesByUserId(user.getId());
    CreatedBy creator = CreatedBy.builder()
        .name(FeedUtil.getUserName(user, petProfiles))
        .userId(user.getId())
        .imageUrl(user.getPhotoUrl())
        .createdAt(timestamp)
        .petBreed("")
        .petName("")
        .imageUrl(StringUtils.isBlank(user.getPhotoUrl()) ? "": user.getPhotoUrl())
        .build();
    PostNotificationDTO postNotification = PostNotificationDTO.builder()
        .post(FeedUtil.toPostDTO(post.getUser().getId(), post, petProfiles))
        .type(notType)
        .createdBy(creator)
        .build();
    for (String fbId : firebaseIds) {
      PostPushNotificationWrapper wrapper = PostPushNotificationWrapper.builder()
          .notification(PushNotificationDTO.builder()
              .body(getBodyOFNotification(notType, data))
              .title(getTitleOfNotification(notType, creator))
              .payload(postNotification)
              .data(postNotification)
              .build())
          .to(fbId)
          .build();
//      System.out.println(not.toString());
//      ObjectMapper mapper = new ObjectMapper();
//      System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(not));
      try {
        restTemplate.exchange(firebaseUrl, HttpMethod.POST,
            new HttpEntity<>(wrapper, new HttpHeaders() {{
              add("Authorization", "key=" + firebaseToken);
              add("Content-Type", "application/json");
            }}), Map.class).getBody();
        pushNotificationRepository.save(PushNotification.builder()
            .type(notType.toString())
            .body(wrapper.getNotification().getBody())
            .title(wrapper.getNotification().getTitle())
            .user(post.getUser())
            .firebaseID(fbId)
            .data(new ObjectMapper().writeValueAsString(wrapper.getNotification().getPayload()))
            .build());
      } catch (Exception e) {
        log.error("Error in sending firebase push notification: {} ", e);
      }
    }
  }

  private String getTitleOfNotification(PostNotificationType notType, CreatedBy creator) {
    String name = creator.getName().trim();
    if (notType.equals(PostNotificationType.COMMENT_ADDED)) {
      return name + " " + "commented on your post!";
    } else if (notType.equals(PostNotificationType.POST_LIKED)) {
      return name + " " + "liked your post!";
    } else if (notType.equals(PostNotificationType.POST_FOLLOWERS)) {
      return name + " " + "commented on a post you follow!";
    } else if (notType.equals(PostNotificationType.POST_BOOKMARKED)) {
      return name + " " + "saved your post!";
    }
    return "";
  }

  private String getBodyOFNotification(PostNotificationType notType, Object data) {
    String comment = (String) data;
    if (notType.equals(PostNotificationType.COMMENT_ADDED)) {
      return StringUtils.isBlank(comment) ? "" : comment;
    } else if (notType.equals(PostNotificationType.POST_LIKED)) {
      return "Tap here to see how the pet parent community is reacting to your post";
    } else if (notType.equals(PostNotificationType.POST_FOLLOWERS)) {
      return StringUtils.isBlank(comment) ? "" : comment;
    } else if (notType.equals(PostNotificationType.POST_BOOKMARKED)) {
      return "Tap here to see how the pet parent community is reacting to your post";
    }
    return "";
  }

  private List<String> getFirebaseIds(Set<User> users) {
    List<User> usersList = new ArrayList<>();
    List<String> uniqueFirebaseIds = new ArrayList<>();
    usersList.addAll(users);
    List<Device> userDevices = deviceRepository.findByUserIn(usersList);
    Set<String> firebaseIds = userDevices.stream().map(Device::getFireBaseId).distinct().collect(
        Collectors.toSet());
    uniqueFirebaseIds.addAll(firebaseIds);
//    firebaseIds.add(
//        "eJ-37l3GZE7Vjv5vCmVqPm:APA91bHp9XUYIv8VtBQQcmqPL2bT0iJ0RNWbyK_gZvG_LobQf4CFgJw0Gy6ilsdjsNaymeYhQWDuxln3oFEPPCA9vGRepn5m44Nw-1LIHak14AaPh0cUuUOibhvC19sCGToY7K_8_hp6");
    return uniqueFirebaseIds;
  }
}
