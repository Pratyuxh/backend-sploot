package com.sploot.api.controller;

import com.sploot.api.config.security.CurrentUser;
import com.sploot.api.model.FileUpload;
import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.CMSEntry;
import com.sploot.api.model.entity.Post;
import com.sploot.api.model.entity.PostMeta;
import com.sploot.api.model.mapper.CMSMapper;
import com.sploot.api.service.CMSService;
import com.sploot.api.service.CommentService;
import com.sploot.api.service.FeedService;
import com.sploot.api.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping(value = "/v1/cms/")
public class CMSController {

    @Autowired
    private CMSService cmsService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private CMSMapper cmsMapper;

    @PostMapping("dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto createCMSEntry(@RequestBody CMSEntryDTO cmsEntryDTO) {
        try {
            CMSEntry cmsEntryRequest = cmsMapper.dtoToEntity(cmsEntryDTO);
            if (cmsEntryDTO.getImage() != null && StringUtils.isNotBlank(
                    cmsEntryDTO.getImage().getByteString())) {
                ImageUploadDTO imageDTO = cmsEntryDTO.getImage();
                FileUpload fileUpload = uploadService.getUrlAfterUpload(imageDTO.getName(), imageDTO.getByteString(), imageDTO.getFormat());
                if (fileUpload != null && fileUpload.getUrl() != null) {
                    cmsEntryRequest.setImageUrl(fileUpload.getUrl());
                }
            }
            CMSEntry cmsEntry = cmsService.createCMS(cmsEntryRequest);
            return new SuccessResponseDto(cmsMapper.entityToDTO(cmsEntry), "CMS Data Created Successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while creating CMS", e);
            return new ErrorResponseDto<>("Error occurred while creating CMS");
        }
    }

    @PutMapping("dashboard/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto updateCMS(@PathVariable("id") long cmsDashboardId, @RequestBody CMSEntryDTO cmsEntryDTO) {
        try {
            CMSEntry cmsEntryRequest = cmsMapper.dtoToEntity(cmsEntryDTO);
            if (cmsEntryDTO.getImage() != null && StringUtils.isNotBlank(
                    cmsEntryDTO.getImage().getByteString())) {
                ImageUploadDTO imageDTO = cmsEntryDTO.getImage();
                FileUpload fileUpload = uploadService.getUrlAfterUpload(imageDTO.getName(), imageDTO.getByteString(), imageDTO.getFormat());
                if (fileUpload != null && fileUpload.getUrl() != null) {
                    cmsEntryRequest.setImageUrl(fileUpload.getUrl());
                }
            }
            CMSEntry cmsEntry = cmsService.updateCMS(cmsDashboardId, cmsEntryRequest);
            return new SuccessResponseDto(cmsEntry, "CMS Data Updated Successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while updating CMS", e);
            return new ErrorResponseDto<>("Error occurred while updating CMS |" + e.getMessage());
        }
    }

    @GetMapping("dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto getCMSByType(@RequestParam String type) {
        try {
            return new SuccessResponseDto(cmsService.getCMSByType(type), "List API Success", 200);
        } catch (Exception e) {
            log.error("Exception occurred while execution of get CMS api", e);
            return new ErrorResponseDto<>("Error occurred while execution of get CMS api|" + e.getMessage());
        }
    }

    @GetMapping("dashboard/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto getCMSByIdAndType(@PathVariable("id") long cmsDashboardId, @RequestParam String type) {
        try {
            return new SuccessResponseDto(cmsService.getCMSByIdAndType(cmsDashboardId, type), "List API Success", 200);
        } catch (Exception e) {
            log.error("Exception occurred while execution of get CMS api", e);
            return new ErrorResponseDto<>("Error occurred while execution of get CMS api|" + e.getMessage());
        }
    }

    @GetMapping("users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto getAllUsers(
        @RequestParam(required = false, defaultValue = "50") String pageSize,
        @RequestParam(required = false, defaultValue = "0") String pageNo) {
        try {
            return new SuccessResponseDto(cmsService.getAllUsers(pageSize, pageNo), "Users fetched successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while fetching Users", e);
            return new ErrorResponseDto<>("Error occurred while fetching users {}",e.getMessage());
        }
    }

    //newly created api by prats
    @PostMapping(path = "posts")
    public ResponseDto createPost(@CurrentUser Principal principal,
                                  @RequestBody Post createPostReq){
        try {
            Post newPost = feedService.createPost(createPostReq);
            return new SuccessResponseDto(newPost, "Post Created Successfully", 201);
        } catch(Exception ex){
            log.error("Exception occurred while creating post for user {}", principal, ex);
            return new ErrorResponseDto<>("Error occurred while creating post");
        }
    }

    //newly created api by prats
    @PutMapping(path = "posts/{postID}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto updatePost(@CurrentUser Principal principal, @PathVariable Long postID,
                                  @ModelAttribute Post createPostReq){
        try {
            Post updatedPost = feedService.updatePost(createPostReq, postID);
            return new SuccessResponseDto(updatedPost,"Post Updated Successfully");
        } catch(Exception ex){
            log.error("Exception occurred while updating post for user {}", principal, ex);
            return new ErrorResponseDto<>(ex.getMessage());
        }
    }

    //newly created api by prats
    //patch mapping is used to update the specific fields
    @PatchMapping("posts/{postID}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto patchPost(@CurrentUser Principal principal, @PathVariable Long postID,
                                 @RequestBody PostMeta patchPostreq){
        try {
            Post updatedPost = feedService.patchPost(patchPostreq, postID);
            return new SuccessResponseDto(updatedPost,"Post Updated Successfully");
        } catch(Exception ex){
            log.error("Exception occurred while creating post for user {}", principal, ex);
            return new ErrorResponseDto<>(ex.getMessage());
        }
    }

    @GetMapping("posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto getAllPosts(
        @RequestParam(required = false, defaultValue = "50") String pageSize,
        @RequestParam(required = false, defaultValue = "0") String pageNo) {
        try {
            return new SuccessResponseDto(cmsService.getAllPosts(pageSize, pageNo), "Posts fetched successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while fetching Posts", e);
            return new ErrorResponseDto<>("Error occurred while fetching posts {}",e.getMessage());
        }
    }

    @DeleteMapping("posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto deletePost(@RequestBody ListOfIds postIds) {
        try {
            return new SuccessResponseDto(cmsService.deletePosts(postIds), "Deleted Posts successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while deleting posts", e);
            return new ErrorResponseDto<>("Error occurred while deleting posts {}", e.getMessage());
        }
    }

    //newly created api by prats
    @PostMapping("/posts/{postID}/thread/{threadID}/comments")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto addComments(@CurrentUser Principal principal,
                                   @PathVariable Long postID,
                                   @PathVariable Long threadID,
                                   @RequestBody AddComment comment,
                                   @RequestParam(required = true) Boolean isRoot){
        try {
            Comment response = feedService.addComment(comment, postID, threadID, isRoot);
            return new SuccessResponseDto(response, "Comment Added Successfully");
        } catch(Exception ex){
            log.error("Exception occurred while creating post for user {}", principal, ex);
            return new ErrorResponseDto<>(ex.getMessage());
        }
    }

    //newly created api by prats
    @PutMapping("/posts/{postID}/thread/{threadID}/comments/{commentID}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto updateComments(@CurrentUser Principal principal,
                                      @PathVariable("postID") Long postID,
                                      @PathVariable("threadID") Long threadID,
                                      @PathVariable("commentID") Long commentID,
                                      @RequestBody AddComment comment){
        try {
            feedService.updateComment(comment,postID,threadID,commentID);
            return new SuccessResponseDto("Comment Updated Successfully");
        } catch(Exception ex){
            log.error("Exception occurred while updating comment for user {}", principal, ex);
            return new ErrorResponseDto<>(ex.getMessage());
        }
    }

    @DeleteMapping("comments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto deleteComments(@RequestBody ListOfIds commentIds) {
        try {
            return new SuccessResponseDto(cmsService.deleteComments(commentIds), "Deleted Comments successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while deleting comments", e);
            return new ErrorResponseDto<>("Error occurred while deleting comments {}", e.getMessage());
        }
    }

    @DeleteMapping("post/{postid}/thread/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto deleteThread(@PathVariable("id") long threadID, @PathVariable("postid") long postID) {
        try {
            return new SuccessResponseDto(cmsService.deleteThread(postID, threadID), "Deleted comment thread successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while deleting comment thread", e);
            return new ErrorResponseDto<>("Error occurred while deleting comment thread {}", e.getMessage());
        }
    }

    @GetMapping("counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto getAllStats(
        @RequestParam(required = true) String from,
        @RequestParam(required = true) String to) {
        try {
            return new SuccessResponseDto(cmsService.getAllStats(from, to), "Stats retrieved successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while retrieving stats", e);
            return new ErrorResponseDto<>("Error occurred while retrieving stats {}", e.getMessage());
        }
    }

    @DeleteMapping("/users/{userID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto deleteUser( @PathVariable("userID") long userID) {
        try {
            cmsService.deleteUser(userID);
            return new SuccessResponseDto( " user deleted successfully"," user deleted successfully", 200);
        } catch (Exception e) {
            log.error("Exception occurred while deleting user", e);
            return new ErrorResponseDto<>("Error occurred while deleting user {}", e.getMessage());
        }
    }
}
