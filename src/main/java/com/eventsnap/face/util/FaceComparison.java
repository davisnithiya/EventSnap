package com.eventsnap.face.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.CompareFacesMatch;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class FaceComparison {

    private static final Logger logger = LoggerFactory.getLogger(FaceComparison.class);

    private final S3Client s3Client;
    private final RekognitionClient rekognitionClient;
    private static final float SIMILARITY_THRESHOLD = 70.0f;

    @Value("${s3.matched-photo-bucket-name}")
    private String s3MatchedPhotoBucketName;

    public FaceComparison(S3Client s3Client, RekognitionClient rekognitionClient) {
        this.s3Client = s3Client;
        this.rekognitionClient = rekognitionClient;
    }

    public void compareFacesFromS3Buckets(String sourceBucketName, String sourceKey, String targetBucketName,
            List<String> targetKeys, int batchSize) {
        try {
            logger.info("Comparing faces from source image: {} to {} target images", sourceKey, targetKeys.size());
            byte[] sourceImageBytes = getImageBytesFromS3(sourceBucketName, sourceKey);
            if (sourceImageBytes == null) {
                logger.error("Failed to retrieve source image from S3");
                return;
            }
            
            Image sourceImage = Image.builder().bytes(SdkBytes.fromByteArray(sourceImageBytes)).build();
            List<List<String>> targetKeyBatches = batchTargetKeys(targetKeys, batchSize);

            for (List<String> targetKeyBatch : targetKeyBatches) {
                List<Image> targetImages = new ArrayList<>();
                for (String targetKey : targetKeyBatch) {
                    byte[] targetImageBytes = getImageBytesFromS3(targetBucketName, targetKey);
                    if (targetImageBytes != null) {
                        Image targetImage = Image.builder().bytes(SdkBytes.fromByteArray(targetImageBytes)).build();
                        targetImages.add(targetImage);
                    }
                }
                compareFacesBatch(sourceImage, targetImages, sourceKey, targetKeyBatch);
            }
        } catch (Exception e) {
            logger.error("Error comparing faces: {}", e.getMessage());
        }
    }

    private List<List<String>> batchTargetKeys(List<String> targetKeys, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < targetKeys.size(); i += batchSize) {
            batches.add(targetKeys.subList(i, Math.min(i + batchSize, targetKeys.size())));
        }
        return batches;
    }

    private byte[] getImageBytesFromS3(String bucketName, String objectKey) {
        try {
            return s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(objectKey).build())
                    .asByteArray();
        } catch (S3Exception e) {
            logger.error("Error getting image bytes from S3: {}", e.getMessage());
            return null;
        }
    }

    private void compareFacesBatch(Image sourceImage, List<Image> targetImages, String sourceKey, List<String> targetKeys) {
        try {
            for (int i = 0; i < targetImages.size(); i++) {
                Image targetImage = targetImages.get(i);
                String targetKey = targetKeys.get(i);
                CompareFacesRequest request = CompareFacesRequest.builder()
                        .sourceImage(sourceImage)
                        .targetImage(targetImage)
                        .build();

                CompareFacesResponse response = rekognitionClient.compareFaces(request);

                List<CompareFacesMatch> matches = response.faceMatches();
                for (CompareFacesMatch match : matches) {
                    logger.info("Face matched with similarity: {}", match.similarity());
                    String folderName = sourceKey.substring(0, sourceKey.lastIndexOf('.'));
                    if (match.similarity() >= SIMILARITY_THRESHOLD) {
                        byte[] targetImageBytes = targetImage.bytes().asByteArray();
                        saveMatchedPhotoToS3(targetImageBytes, targetKey, folderName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error comparing faces: {}", e.getMessage());
        }
    }

    private void saveMatchedPhotoToS3(byte[] imageBytes, String objectKey, String folderName) {
        try {
            String uniqueKey = folderName + "/" + objectKey;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(s3MatchedPhotoBucketName)
                    .key(uniqueKey).build();

            // Upload the matched photo to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
            logger.info("Matched photo uploaded to S3:Folder={}, Key={}", folderName, objectKey);
        } catch (S3Exception e) {
            logger.error("Error uploading matched photo to S3: {}", e.getMessage());
        }
    }
}
