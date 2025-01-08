# EventSnap

## App Overview

EventSnap is a specialized Spring Boot application designed to capture and cherish memorable moments from  event. 

### Memories Feature

The core functionality of EventSnap revolves around the Memories feature:

- **Personalized Collections:** EventSnap generates personalized collections of photos and videos for each person, combining them into a captivating visual experience.

### EventSnap Screen

The EventSnap screen is the central hub for reliving and sharing captured memories. Delegates can upload and manage their images, ensuring that each special moment is preserved for posterity.

### Profile Screen

The Profile screen provides delegates with the ability to customize their personal information and image. Delegates can add their images using two convenient methods:

1. **Upload from Gallery:** Delegates can choose to upload an image from their device's photo gallery, ensuring that their profile reflects their unique personality.

2. **Capture by Camera:** Delegates can capture a new image using the device's camera, instantly updating their profile with a fresh snapshot.

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
   ```bash
   git clone https://gitlab.com/koko-microservices/smartevents.git
   cd pedimoments
2. **Configure Application Properties:**
   Update the application.properties file with the correct values for photo.path 
3. **Build and Run the Application:**
	./mvnw clean install
	./mvnw spring-boot:run
4. **Access points:**
  http://localhost:8081//smart-events/api/v1/swagger-ui/index.html
  
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
   





  
