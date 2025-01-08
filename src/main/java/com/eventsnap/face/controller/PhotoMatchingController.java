package com.eventsnap.face.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventsnap.face.scheduler.PhotoMatchingScheduler;


@RestController
public class PhotoMatchingController {
	
	@Autowired
	private PhotoMatchingScheduler matchingScheduler;

	@GetMapping("/eventsnap/match-photos-scheduler")
	public String getMatchingPhotosScheduler() {

		matchingScheduler.performPhotoMatching();
		return "Photo Matching Complted";
	}

}
