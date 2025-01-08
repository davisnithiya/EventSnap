package com.eventsnap.face.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsnap.face.entity.MatchedPhotos;
import com.eventsnap.face.repo.MatchedPhotosRepository;

@Service
public class MatchedPhotosService {

    @Autowired
    private MatchedPhotosRepository matchedPhotosRepository;

    public Long getContactIdForMatchedPhoto(String targetObjectKey) {
    	Long photoId = Long.parseLong(targetObjectKey);
        Optional<MatchedPhotos> matchedPhotoOptional = matchedPhotosRepository.findById(photoId);
        
        if (matchedPhotoOptional.isPresent()) {
            MatchedPhotos matchedPhoto = matchedPhotoOptional.get();
            return matchedPhoto.getContact().getId();
        } else {
            
            return null; 
        }
    }
}
