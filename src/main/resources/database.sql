CREATE SCHEMA eventsnap;

CREATE TABLE eventsnap.contact (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    photo LONGBLOB,
    photoUrl VARCHAR(255),
    isNotificationSent BOOLEAN DEFAULT FALSE
);

select * from eventsnap.contact;

CREATE TABLE eventsnap.candid_photos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255)
);

select * from eventsnap.candid_photos;


CREATE TABLE eventsnap.match_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contact_id BIGINT,
    candid_photo_id INT,
    FOREIGN KEY (contact_id) REFERENCES contact(id),
    FOREIGN KEY (candid_photo_id) REFERENCES candid_photos(id)
);

select * from eventsnap.match_photos;
