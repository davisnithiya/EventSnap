package com.eventsnap.face.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.eventsnap.face.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	// Query method to retrieve email address by username
	@Query("SELECT c.email FROM Contact c WHERE c.name = :userName")
	String findEmailByUsername(@Param("userName") String userName);

// Query method to check if notification is sent for a given username
	@Query("SELECT c.isNotificationSent FROM Contact c WHERE c.name = :userName")
	boolean isNotificationSentForUsername(@Param("userName") String userName);

// Method to mark notification as sent for a given username
	@Modifying
	@Transactional
	@Query("UPDATE Contact c SET c.isNotificationSent = true WHERE c.name = :userName")
	void markNotificationSentForUsername(@Param("userName") String userName);
	
	 Optional<Contact> findByEmail(String email);
}
