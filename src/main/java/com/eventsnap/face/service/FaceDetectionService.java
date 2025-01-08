package com.eventsnap.face.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectFacesRequest;
import software.amazon.awssdk.services.rekognition.model.DetectFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.FaceDetail;
import software.amazon.awssdk.core.SdkBytes;

import java.util.List;

@Service
public class FaceDetectionService {

    private final RekognitionClient rekognitionClient;

    public FaceDetectionService(RekognitionClient rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }

    public int detectFaces(byte[] imageBytes) {
        Image image = Image.builder()
                           .bytes(SdkBytes.fromByteArray(imageBytes))
                           .build();

        DetectFacesRequest request = DetectFacesRequest.builder()
                                                       .image(image)
                                                       .build();

        DetectFacesResponse result = rekognitionClient.detectFaces(request);
        List<FaceDetail> faceDetails = result.faceDetails();

        return faceDetails.size();
    }
}
