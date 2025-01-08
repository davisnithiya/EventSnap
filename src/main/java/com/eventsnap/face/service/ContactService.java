package com.eventsnap.face.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eventsnap.face.entity.Contact;
import com.eventsnap.face.repo.ContactRepository;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class ContactService {

	@Value("${s3.source-bucket-name}")
	private String s3SourceBucketName;

	@Autowired
	private ContactRepository contactRepository;

	private final Logger logger = LoggerFactory.getLogger(ContactService.class);

	public Contact saveContact(Contact contact) {
		return contactRepository.save(contact);
	}

	public Contact getContactDetails(Long contactId) {
		return contactRepository.findById(contactId).orElse(null);
	}

	public Contact getContactDetailsDynamically() {
		List<Contact> contacts = contactRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		if (!contacts.isEmpty()) {
			return contacts.get(0);
		} else {
			return null;
		}
	}
	
	public boolean contactExists(String email) {
		Optional<Contact> existingContact = contactRepository.findByEmail(email);
		return existingContact.isPresent();
    }

	public String uploadToAmazonS3(byte[] photoBytes, String name) {
	    String bucketName = s3SourceBucketName;
	    String key = name + ".JPG";

	    try {
	        // Initialize Amazon S3 client with explicitly set credentials
	        S3Client s3Client = S3Client.builder()
	                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
	                .build();

	        // Set object metadata
	        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
	                .bucket(bucketName)
	                .key(key)
	                .build();

	        // Upload the object to S3
	        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(photoBytes));

	        // Return the S3 object URL
	        String objectUrl = s3Client.utilities().getUrl(
	                GetUrlRequest.builder()
	                        .bucket(bucketName)
	                        .key(key)
	                        .build())
	                .toString();

	        logger.info("Uploaded image to Amazon S3: {}", objectUrl);
	        return objectUrl;
	    } catch (S3Exception e) {
	        logger.error("Error uploading image to Amazon S3", e);
	        return null;
	    } catch (SdkClientException e) {
	        logger.error("Amazon S3 client exception", e);
	        return null;
	    }
	}

}
