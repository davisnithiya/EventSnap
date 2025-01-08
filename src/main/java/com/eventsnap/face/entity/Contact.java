package com.eventsnap.face.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Id;


@Entity
@Table(name = "contact")
public class Contact {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "email")
    private String email;
	
    @Lob
	@Column(name = "photo")
    private byte[] photo;
    
    @Column(name = "photo_Url")
	private String photoUrl;
    
    @Column(name = "is_notification_sent")
    private boolean isNotificationSent;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public boolean isNotificationSent() {
		return isNotificationSent;
	}
	public void setNotificationSent(boolean isNotificationSent) {
		this.isNotificationSent = isNotificationSent;
	}

}
