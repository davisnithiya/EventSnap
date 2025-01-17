# EventSnap

## App Overview

EventSnap is a specialized Spring Boot application built to capture and preserve memorable moments from events. It leverages advanced aws face recognition technology to group similar faces, ensuring that every precious moment is organized and easy to navigate.

### Profile Customization

Delegates can personalize their profile by choosing one of the following options:

1. **Select from Gallery:** They can pick an image from their phone's gallery to update their profile.

2. **Take a New Photo:** Delegates can take a new picture using their device’s camera for an instant profile update.

## Prerequisites

Before using EventSnap, ensure the following prerequisites are met:

- **Update Application Properties:**
  - `photo.path:` Provide the path for photos captured during the event.
  
- ** Database Table creation
	- refer database.sql file
	
- ** create your access key from aws
	steps:
		1. IAM -> users -> select user -> create access key -> save this access key and secret key
	- download aws cli
		open cmd then 
		aws configure
		paste your access and secretkey . 
		this will create config and credenatils file in your .aws folder
		

## Getting Started

To get started with following steps:

1. **Clone the Repository:**
2. **Configure Application Properties:**
   Update the application.properties file with the correct values for photo.path 
3. **Build and Run the Application:**
	./mvnw clean install
	./mvnw spring-boot:run
  
# Spring Boot User Registration and Photo Matching Flow

## User Registration Endpoint
1. Client sends a POST request to `/contacts` endpoint.
2. Server receives user registration details (photo and delegateId).
4. The user details are stored in MySQL database.

## Photo Matching Scheduler
1. Scheduler runs can be triggerd from Swagger
2. For each registered user:
   a. upload all the photos from the local photo path to photo table
   b. compare the contact table and photo table 
   c. save the matching photos into contact_photo table
   d. upload this photos in the google cloud bucket in each folder based on there deligate id
   e. send push notification to the user
   





  
