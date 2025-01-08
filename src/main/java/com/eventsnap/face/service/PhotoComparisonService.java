package com.eventsnap.face.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsnap.face.entity.Contact;
import com.eventsnap.face.entity.MatchedPhotos;
import com.eventsnap.face.entity.Photo;
import com.eventsnap.face.repo.ContactRepository;
import com.eventsnap.face.repo.MatchedPhotosRepository;
import com.eventsnap.face.repo.PhotoRepository;

@Service
public class PhotoComparisonService {
	private final Logger logger = LoggerFactory.getLogger(PhotoComparisonService.class);
	@Autowired
	MatchedPhotosRepository matchedPhotosRepository;
	
	@Autowired
	PhotoRepository photoRepository;
	
	@Autowired
	ContactRepository contactRepository;

	public void updateMatchedPhotos(Long contactId, String photoObjectKey) {
	    try {
	        // Find the photo by its object key
	        Optional<Photo> optionalPhoto = photoRepository.findByFileName(photoObjectKey);

	        if (optionalPhoto.isPresent()) {
	            // Retrieve contact details using the provided contactId
	            Optional<Contact> optionalContact = contactRepository.findById(contactId);

	            if (optionalContact.isPresent()) {
	                MatchedPhotos matchedPhotos = new MatchedPhotos();
	                matchedPhotos.setContact(optionalContact.get());
	                matchedPhotos.setPhoto(optionalPhoto.get());

	                // Save the matched photo
	                matchedPhotosRepository.save(matchedPhotos);
	                logger.info("Matched photo saved to the MatchedPhotos table");
	            } else {
	                logger.error("Contact not found with ID: {}", contactId);
	            }
	        } else {
	            logger.error("Photo not found with object key: {}", photoObjectKey);
	        }
	    } catch (Exception e) {
	        logger.error("Error saving matched photo to the MatchedPhotos table: {}", e.getMessage());
	    }
	}


}
