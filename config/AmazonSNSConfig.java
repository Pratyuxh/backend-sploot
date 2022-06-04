package com.sploot.api.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonSNSConfig {

    @Value("${cloud.aws.access-key}")
    private String accessKey;

    @Value("${cloud.aws.access-secret}")
    private String accessSecret;

  @Bean
  @Primary
  public AmazonSNSClient amazonSNSClient() {
    return (AmazonSNSClient) AmazonSNSClientBuilder
        .standard()
        .withRegion(Regions.AP_SOUTH_1)
        .withCredentials(
            new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                    accessKey,
                    accessSecret
                )
            )
        )
        .build();
  }

}