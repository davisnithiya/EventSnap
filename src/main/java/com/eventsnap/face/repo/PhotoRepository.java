package com.eventsnap.face.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventsnap.face.entity.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
	Optional<Photo> findByFileName(String fileName);
}

