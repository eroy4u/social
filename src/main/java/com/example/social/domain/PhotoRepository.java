package com.example.social.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PhotoRepository extends CrudRepository<Photo, Long>{
	
	List<Photo> findByUserId(String userId);

}
