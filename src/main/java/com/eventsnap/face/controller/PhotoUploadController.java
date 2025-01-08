package com.eventsnap.face.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventsnap.face.entity.Photo;
import com.eventsnap.face.repo.PhotoRepository;

import net.coobird.thumbnailator.Thumbnails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RestController
public class PhotoUploadController {

	@Autowired
	PhotoRepository photoRepository;

	@Value("${photo.path}")
	private String localImagePath;

	@Value("${s3.target-bucket-name}")
	private String s3TargetBucketName;

	private final S3Client s3Client;
	private final Logger logger = LoggerFactory.getLogger(PhotoUploadController.class);

	public PhotoUploadController(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	@PostMapping("/eventsnap/s3upload")
	public String uploadImagesToS3(@RequestParam("localImagePath") String localImagePath) {
		File directory = new File(localImagePath);
		File[] files = directory.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					try {
						byte[] compressedImageBytes = compressImage(file);

						String key = file.getName();
						int lastIndex = key.lastIndexOf(".");
						if (lastIndex != -1) {
							key = key.substring(0, lastIndex) + ".JPG";
						} else {
							key += ".JPG";
						}

						// Create PutObjectRequest
						PutObjectRequest request = PutObjectRequest.builder().bucket(s3TargetBucketName).key(key)
								.build();

						// Upload file to S3
						s3Client.putObject(request, RequestBody.fromBytes(compressedImageBytes));
						Photo photo = new Photo();
						photo.setFileName(key);
						photoRepository.save(photo);
						logger.info("Images are uploaded", key);
					} catch (IOException e) {
						e.printStackTrace();
						return "Error processing image: " + e.getMessage();
					}
				}
			}
		}
		return "Upload successful";
	}

	private byte[] compressImage(File imageFile) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Thumbnails.of(imageFile).size(800, 600).outputQuality(0.8).outputFormat("JPG").toOutputStream(outputStream);

		return outputStream.toByteArray();
	}

}
