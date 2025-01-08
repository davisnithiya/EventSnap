package com.eventsnap.face.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {
	@Bean
    public S3Client s3Client() {
		
		S3Client s3Client = S3Client.builder()
                .region(Region.AP_SOUTH_1)  
                .build();
		AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        AwsCredentials credentials = credentialsProvider.resolveCredentials();
		 System.out.println("Access Key ID: " + credentials.accessKeyId());

	        return s3Client;
    }

	 @Bean
	    public RekognitionClient rekognitionClient() {
	        RekognitionClient rekognitionClient = RekognitionClient.builder()
	                .region(Region.AP_SOUTH_1) 
	                .credentialsProvider(DefaultCredentialsProvider.create())
	                .build();
	        return rekognitionClient;
	    }
	}
