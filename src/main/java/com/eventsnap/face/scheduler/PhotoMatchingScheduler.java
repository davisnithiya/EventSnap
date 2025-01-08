package com.eventsnap.face.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eventsnap.face.repo.ContactRepository;
import com.eventsnap.face.service.EmailService;
import com.eventsnap.face.util.FaceComparison;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Component
public class PhotoMatchingScheduler {

	@Value("${s3.source-bucket-name}")
	private String s3SourceBucketName;

	@Value("${s3.target-bucket-name}")
	private String s3TargetBucketName;

	@Value("${s3.matched-photo-bucket-name}")
	private String s3MatchedPhotoBucketName;
	
	@Value("${s3.original-photo-bucket-name}")
    private String s3OriginalPhotoBucketName;

	private static final int MAX_KEYS_PER_REQUEST = 1000;
	private final Logger logger = LoggerFactory.getLogger(PhotoMatchingScheduler.class);
	private final S3Client s3Client;
	private final FaceComparison faceComparison;
	private final EmailService emailService;
	private final ContactRepository contactRepository;

	@Autowired
	public PhotoMatchingScheduler(S3Client s3Client, FaceComparison faceComparison, EmailService emailService,
			ContactRepository contactRepository) {
		this.s3Client = s3Client;
		this.faceComparison = faceComparison;
		this.emailService = emailService;
		this.contactRepository = contactRepository;
	}

	public void performPhotoMatching() {

		// Step 1: Compare photos
/*		List<String> sourceKeys = getObjectKeysFromS3(s3SourceBucketName);
		List<String> targetKeys = getObjectKeysFromS3(s3TargetBucketName);

		int batchSize = 10;

		// Iterate over source images
		for (String sourceKey : sourceKeys) {
			// Compare source image with all target images
			faceComparison.compareFacesFromS3Buckets(s3SourceBucketName, sourceKey, s3TargetBucketName, targetKeys,
					batchSize);
		}*/
		
		

		// step 2: send notification
		sendEmailsToMatchedUsers();
	}

	private void sendEmailsToMatchedUsers() {
		// Fetch matched users and their email addresses
		List<String> matchedUsers = getMatchedUsers();

		  for (String userName : matchedUsers) {
	            String recipientEmail = contactRepository.findEmailByUsername(userName);
	            if (recipientEmail != null && !contactRepository.isNotificationSentForUsername(userName)) {
	                List<byte[]> originalPhotos = getOriginalPhotosForUser(userName);
	                try {
	                    // Send email with original photos attached
	                    emailService.sendEmailWithPhotos(recipientEmail, originalPhotos);
	                    contactRepository.markNotificationSentForUsername(userName);
	                } catch (Exception e) {
	                    logger.error("Error sending email to {}: {}", recipientEmail, e.getMessage());
	                }
	            }
	        }
	    }

	private List<String> getMatchedUsers() {
		List<String> matchedUsers = new ArrayList<>();

		// Fetch folder names (which represent usernames) from the matched photos S3
		// bucket
		List<String> usernames = getUsernamesFromS3Bucket(s3MatchedPhotoBucketName);

		// Add usernames (which represent matched users) to the list
		matchedUsers.addAll(usernames);

		return matchedUsers;
	}

	private List<String> getUsernamesFromS3Bucket(String bucketName) {
		List<String> usernames = new ArrayList<>();

		try {
			ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder().bucket(bucketName).build();

			ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

			listObjectsResponse.contents().forEach(object -> {
				// Extract username from the object key (which is actually the folder name)
				String username = extractUsername(object.key());
				usernames.add(username);
			});

		} catch (S3Exception e) {
			logger.error("Error listing objects in S3 bucket {}: {}", bucketName, e.getMessage());
		}

		return usernames;
	}

	private String extractUsername(String key) {
		// Split the key by "/"
		String[] parts = key.split("/");
		// Return the first part, which should be the username (folder name)
		return parts[0];
	}

	private List<String> getObjectKeysFromS3(String bucketName) {
		try {
			List<String> objectKeys = new ArrayList<>();
			ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder().bucket(bucketName)
					.maxKeys(MAX_KEYS_PER_REQUEST).build();

			// Paginate through the objects in the S3 bucket
			ListObjectsV2Iterable listObjectsResponses = s3Client.listObjectsV2Paginator(listObjectsRequest);
			listObjectsResponses.stream().flatMap(response -> response.contents().stream()).map(S3Object::key)
					.forEach(objectKeys::add);

			return objectKeys;
		} catch (S3Exception e) {
			logger.error("Error listing objects in S3 bucket {}: {}", bucketName, e.getMessage());
			return Collections.emptyList();
		}
	}

	private List<byte[]> getMatchedPhotosForUser(String userName) {
		List<byte[]> matchedPhotos = new ArrayList<>();

		try {
			// S3Client s3Client = S3Client.create();

			// Assuming each user has their own folder in the S3 bucket
			String folderName = userName + "/";

			ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder().bucket(s3MatchedPhotoBucketName)
					.prefix(folderName).build();

			ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

			listObjectsResponse.contents().forEach(object -> {
				// Retrieve photo data for each object in the folder
				byte[] photoData = retrievePhotoData(s3MatchedPhotoBucketName, object.key());
				matchedPhotos.add(photoData);
			});

		} catch (S3Exception e) {
			logger.error("Error listing objects for user {} in S3 bucket {}: {}", userName, s3MatchedPhotoBucketName,
					e.getMessage());
		}

		return matchedPhotos;
	}

	private byte[] retrievePhotoData(String bucketName, String key) {
		try {
			// S3Client s3Client = S3Client.create();

			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

			ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
			InputStream inputStream = responseInputStream;

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			return outputStream.toByteArray();

		} catch (IOException | S3Exception e) {
			logger.error("Error retrieving photo data from S3 bucket {}: {}", bucketName, e.getMessage());
			return new byte[0];
		}
	}
	
	private List<byte[]> getOriginalPhotosForUser(String userName) {
        List<byte[]> originalPhotos = new ArrayList<>();

        try {
            // Assuming each user has their own folder in the matched photo S3 bucket
            String folderName = userName + "/";

            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder().bucket(s3MatchedPhotoBucketName)
                    .prefix(folderName).build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            for (S3Object object : listObjectsResponse.contents()) {
                // Extract the photo filename
                String filename = object.key().substring(folderName.length());
                // Retrieve photo data for the original photo using the filename
                byte[] photoData = retrievePhotoData(s3OriginalPhotoBucketName, filename);
                originalPhotos.add(photoData);
            }

        } catch (S3Exception e) {
            logger.error("Error listing objects for user {} in S3 bucket {}: {}", userName, s3MatchedPhotoBucketName,
                    e.getMessage());
        }

        return originalPhotos;
    }

}
