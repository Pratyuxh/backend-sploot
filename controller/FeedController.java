package com.sploot.api.controller;

import com.sploot.api.config.security.CurrentUser;
import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.PetProfile;
import com.sploot.api.model.entity.Post;
import com.sploot.api.model.entity.PostMeta;
import com.sploot.api.model.mapper.PostMapper;
import com.sploot.api.service.FeedService;
import com.sploot.api.service.PetService;
import com.sploot.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.sploot.api.util.FeedUtil.toPostDTO;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @Autowired
  private UserService userService;

  @Autowired
  private PetService petService;

  @Autowired
  private PostMapper postMapper;


  //TODO kapil move all the post related mappers to mapstruct as for other
  @PostMapping(path = "/posts")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto createPost(@RequestBody PostDTO createPostReqDTO){
    try {
      Post createPostReq = postMapper.dtoToEntity(createPostReqDTO);
      Post createdPost = feedService.createPost(createPostReq);
      Long loggedInUserId = userService.getLoggedInUserId();
      List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);
      PostDTO newPostDTO = toPostDTO(loggedInUserId, createdPost, petProfiles);
      return new SuccessResponseDto(newPostDTO, "Post Created Successfully", 201);
    } catch(Exception ex){
      log.error("Exception occurred while creating post for user {}", ex);
      return new ErrorResponseDto<>("Error occurred while creating post");
    }
  }

  @PutMapping(path = "/posts/{postID}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto updatePost(@PathVariable Long postID,
      @ModelAttribute PostDTO updatePostReqDTO){
    try {
      Post updatePostReq = postMapper.dtoToEntity(updatePostReqDTO);
      Post updatedPost = feedService.updatePost(updatePostReq, postID);
      Long loggedInUserId = userService.getLoggedInUserId();
      List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);
      PostDTO updatedPostDTO = toPostDTO(loggedInUserId, updatedPost, petProfiles);
      return new SuccessResponseDto(updatedPostDTO,"Post Updated Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while creating post for user {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @PatchMapping("/posts/{postID}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto patchPost(@PathVariable Long postID, @RequestBody PostMeta patchPostreq){
    try {
      Post updatedPost = feedService.patchPost(patchPostreq, postID);
      Long loggedInUserId = userService.getLoggedInUserId();
      List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);
      PostDTO updatedPostDTO = toPostDTO(loggedInUserId, updatedPost, petProfiles);
      return new SuccessResponseDto(updatedPostDTO,"Post Updated Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while creating post for user {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @DeleteMapping("/posts/{postID}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto deletePost(@PathVariable Long postID){
    try {
      feedService.deletePost(postID);
      return new SuccessResponseDto("Post Deleted Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while creating post for user {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @GetMapping("/posts")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto getAllPosts(
      @RequestParam(required = false, defaultValue = "50") String pageSize,
      @RequestParam(required = false, defaultValue = "0") String pageNo,
      @RequestParam(required = false, defaultValue = "false") Boolean isBookmarked,
      @RequestParam(required = false, defaultValue = "false") Boolean isCreator,
      @RequestParam(required = false, defaultValue = "false") Boolean isMemory,
      @RequestParam(required = false) String search){
    try {

      Integer limit = Integer.parseInt(pageSize);
      Integer pageNumber = Integer.parseInt(pageNo) > 0 ? Integer.parseInt(pageNo) - 1: 0;
      Integer size = Integer.parseInt(pageSize) > 0 ? Integer.parseInt(pageSize) - 1: 50;
      Integer offset = pageNumber * Integer.parseInt(pageSize);
      Long loggedInUserId = userService.getLoggedInUserId();
      List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);

      if(isMemory){
        List<Post> postEntities = feedService.findAllByIsMemory(pageNumber, size);
        List<PostDTO> postDTOS = postEntities.stream()
                .map(ps -> toPostDTO(loggedInUserId, ps, petProfiles))
                .collect(Collectors.toList());
        return new SuccessResponseDto(postDTOS,"Post fetched Successfully");

      }

      if(isCreator){
        List<Post> postEntities = feedService.findAllByUser(pageNumber, size);
        List<PostDTO> postDTOS = postEntities.stream()
                .map(ps -> toPostDTO(loggedInUserId, ps, petProfiles))
                .collect(Collectors.toList());
        return new SuccessResponseDto(postDTOS,"Post fetched Successfully");

      }

      if (!isBookmarked) {
        if (StringUtils.hasLength(search)) {
          List<Post> posts = feedService.getAllPostsByTags(search, pageSize, pageNo);
          List<PostDTO> postDTOS = posts.stream()
                  .skip(offset)
                  .limit(limit)
                  .map(ps -> toPostDTO(loggedInUserId, ps, petProfiles))
                  .collect(Collectors.toList());
          return new SuccessResponseDto(postDTOS,"Post fetched Successfully");

        } else {

          Page<Post> postPage = feedService.getAllPosts(pageSize, pageNo);
          List<PostDTO> postDTOS = postPage.getContent().stream()
                  .map(ps -> toPostDTO(loggedInUserId, ps, petProfiles))
                  .collect(Collectors.toList());
          return new SuccessResponseDto(postDTOS, "Post fetched Successfully");
        }
      } else {
        //TODO kapil major issue with below method, it gets all posts
        List<Post> posts = feedService.getAllBookMarkedPosts();
        List<PostDTO> postDTOS = posts.stream()
                .skip(offset)
                .limit(limit)
                .map(ps -> toPostDTO(loggedInUserId, ps, petProfiles))
                .collect(Collectors.toList());
        return new SuccessResponseDto(postDTOS,"Post fetched Successfully");
      }

    } catch(Exception ex){
      log.error("Exception occurred while creating post for user {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @GetMapping("/feed")
  public ResponseDto getFeed(
          @RequestParam(required = false, defaultValue = "50") String pageSize,
          @RequestParam(required = false, defaultValue = "0") String pageNo,
          @RequestParam(required = false, defaultValue = "false") Boolean isBookmarked,
          @RequestParam(required = false) String search){
    try {
      Long loggedInUserId = userService.getLoggedInUserId();
      List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);
      Integer pageNumber = Integer.parseInt(pageNo) > 0 ? Integer.parseInt(pageNo) - 1: 0;
      Integer size = Integer.parseInt(pageSize) > 0 ? Integer.parseInt(pageSize) - 1: 50;

      List<Post> postEntities = feedService.findAllByUser(pageNumber, size);
      List<PostDTO> postDTOS = postEntities.stream()
              .map(ps -> toPostDTO(loggedInUserId, ps, petProfiles))
              .collect(Collectors.toList());
      return new SuccessResponseDto(postDTOS,"Feed fetched Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while retrieving feed {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }


  @GetMapping("/posts/{postID}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto getPostById(@PathVariable Long postID){
    try {
      Post post = feedService.getPostById(postID);
      Long loggedInUserId = userService.getLoggedInUserId();
      List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);
      PostDTO postDTO = toPostDTO(loggedInUserId, post, petProfiles);
      return new SuccessResponseDto(postDTO,"Post fetched Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while creating post for user {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }


  @GetMapping("/posts/{postID}/comments")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto getPostComments(
      @PathVariable("postID") Long postID,
      @RequestParam(required = false, defaultValue = "5000") String pageSize,
      @RequestParam(required = false, defaultValue = "0") String pageNo){
    try {
      List<CommentHierarchy> posts = feedService.getAllCommentsOfPost(pageSize, pageNo, postID);
      return new SuccessResponseDto(posts,"Comments fetched Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while creating post for {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @PostMapping("/posts/{postID}/thread/{threadID}/comments")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto addComments(
      @PathVariable Long postID,
      @PathVariable Long threadID,
      @RequestBody AddComment comment,
      @RequestParam(required = true) Boolean isRoot){
    try {
      Comment response = feedService.addComment(comment, postID, threadID, isRoot);
      return new SuccessResponseDto(response, "Comment Added Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while creating post{}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @PutMapping("/posts/{postID}/thread/{threadID}/comments/{commentID}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto updateComments(
      @PathVariable("postID") Long postID,
      @PathVariable("threadID") Long threadID,
      @PathVariable("commentID") Long commentID,
      @RequestBody AddComment comment){
    try {
      feedService.updateComment(comment, postID, threadID, commentID);
      return new SuccessResponseDto("Comment Updated Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while updating comment{}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @PatchMapping("/comments/{commentID}")
  public ResponseDto patchComment(@CurrentUser Principal principal,
                                  @PathVariable Long commentID,  @RequestParam(required = true) Boolean isLiked){
    try {
      return new SuccessResponseDto( feedService.patchComment(isLiked, commentID),"Comment Updated Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while updating comment for user {}", principal, ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }

  @DeleteMapping("/posts/{postID}/thread/{threadID}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseDto deleteComments(
      @PathVariable Long postID,
      @PathVariable Long threadID,
      @RequestParam(required = false) String commentID){
    try {
      feedService.deleteComments(postID, threadID, commentID);
      return new SuccessResponseDto( "Comment Deleted Successfully");
    } catch(Exception ex){
      log.error("Exception occurred while updating comment {}", ex);
      return new ErrorResponseDto<>(ex.getMessage());
    }
  }
}
