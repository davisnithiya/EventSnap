package com.eventsnap.face.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventsnap.face.entity.MatchedPhotos;

public interface MatchedPhotosRepository extends JpaRepository<MatchedPhotos,Long>{
	MatchedPhotos findByPhotoId(Long photoId);

}
