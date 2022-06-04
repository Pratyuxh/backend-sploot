package com.sploot.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sploot.api.model.FileUpload;
import com.sploot.api.util.Utility;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class S3service {

  @Autowired
  private AmazonS3 amazonS3;

  @Value("${cloud.aws.s3-url}")
  private String awsS3Url;


  @Value("${cloud.aws.current-env}")
  private String currentEnvironment;

  private static final String SUFFIX = "/";

  public String bucketName() {
    return "sploot-" + currentEnvironment + "-env";
  }


  public Optional<FileUpload> getUrlAfterPhotoUpload(String photoBytes, String photoFormat, String imageName) throws IOException {
    imageName = (StringUtils.hasLength(imageName)) ? String.valueOf(System.currentTimeMillis()) : imageName;
    String imageFileName = imageName + "_" + new RandomString().nextString() + "." + photoFormat;
    File recreatedFile = Utility.getImageFromBase64String(photoBytes, imageFileName, photoFormat);
    try {
      return Optional.of(new FileUpload(uploadFilePublic(bucketName(), "images", recreatedFile, imageFileName), imageFileName));
    } catch (Exception e) {
      log.error("Exception in uploading file to S3: {}", e);
    } finally {
      recreatedFile.delete();
    }
    return Optional.empty();
  }

  public String uploadFilePublic(String bucketName, String folderName, File file, String fileNameToPut) throws Exception {
    log.info("uploading to bucket - {}, foldername - {}, fileName - {}", bucketName, folderName, fileNameToPut);
    amazonS3.putObject(new PutObjectRequest(bucketName, folderName + "/" + fileNameToPut, file).withCannedAcl(
        CannedAccessControlList.PublicRead));
    return awsS3Url + SUFFIX + folderName + SUFFIX + fileNameToPut;
  }
}
