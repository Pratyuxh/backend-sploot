package com.sploot.api.service;

import com.sploot.api.model.FileUpload;
import com.sploot.api.model.ImageUpload;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    String uploadFile(MultipartFile file) throws IOException;

    FileUpload getUrlAfterUpload(ImageUpload imageUpload) throws IOException;

    FileUpload getUrlAfterUpload(String name, String byteString, String format) throws IOException;

}
