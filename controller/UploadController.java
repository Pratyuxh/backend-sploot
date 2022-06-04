package com.sploot.api.controller;


import com.sploot.api.model.FileUpload;
import com.sploot.api.model.dto.ErrorResponseDto;
import com.sploot.api.model.dto.ImageUploadDTO;
import com.sploot.api.model.dto.ResponseDto;
import com.sploot.api.model.dto.SuccessResponseDto;
import com.sploot.api.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
public class UploadController {
	@Autowired
	UploadService uploadService;


	// TODO kapil move this to client side, and create api to get s3 signed url.
	@PostMapping(path = "/v1/video/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto uploadVideo(@RequestBody MultipartFile file){
		try {
			String url = uploadService.uploadFile(file);
			return new SuccessResponseDto(url, "Video uploaded Successfully", 200);
		} catch(Exception ex){
			log.error("Exception occurred while uploading video for user {}", ex);
			return new ErrorResponseDto<>("Error occurred while uploading  video");
		}
	}

	@PostMapping("/v1/photos")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto<FileUpload> getUploadedImageUrl(@RequestBody final ImageUploadDTO dto) throws IOException {
		log.info("Image Upload reminder CO added is {}",dto);
		FileUpload result = uploadService.getUrlAfterUpload(dto.getName(), dto.getByteString(), dto.getFormat());
		if (result == null) return new ErrorResponseDto<>(
				"Could not Upload Image");
		return new SuccessResponseDto<>(result, "Image Successfully uploaded");
	}
}
