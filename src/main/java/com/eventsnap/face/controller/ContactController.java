package com.eventsnap.face.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventsnap.face.entity.Contact;
import com.eventsnap.face.service.ContactService;
import com.eventsnap.face.service.FaceDetectionService;

@RestController
@RequestMapping("/eventsnap/contacts")
public class ContactController {
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private FaceDetectionService faceDetectionService;


	@PostMapping
	public ResponseEntity<String> addContact(@RequestParam(name = "photo", required = false) MultipartFile photo,
			@RequestParam("name") String name, @RequestParam("email") String email) {
		try {
			 // Check if the email already exists
	        if (contactService.contactExists(email)) {
	            return ResponseEntity.badRequest().body("Email already exists");
	        }
			String photoUrl = null;
			if (photo != null) {
				byte[] photoBytes = photo.getBytes();
				// Check if the photo contains more than one person
                int numberOfFaces = faceDetectionService.detectFaces(photoBytes);
                if (numberOfFaces > 1) {
                    return new ResponseEntity<>("Photo contains more than one person", HttpStatus.BAD_REQUEST);
                }
				photoUrl = contactService.uploadToAmazonS3(photoBytes, name);
			}
			Contact contact = new Contact();
			contact.setName(name);
			contact.setEmail(email);
			contact.setPhotoUrl(photoUrl);
			contactService.saveContact(contact);
			return ResponseEntity.ok("Contact added successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding contact");
		}
	}
}
