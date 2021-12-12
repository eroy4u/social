package com.example.social;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;


import com.example.social.domain.Photo;
import com.example.social.domain.PhotoRepository;
import com.example.social.domain.ResizeRequest;
import com.example.social.utils.S3Utils;

@RestController
public class PhotoController {
	
	@Autowired
	private PhotoRepository photoRespository;
	
	@GetMapping("/photo")
	public Iterable<Photo> all(){
		return photoRespository.findAll();
	}
	
	@PostMapping("/resize")	
	public String resize(@RequestBody ResizeRequest haha) {
		//1. take photo from the position
		//2. 
		
//		System.out.println(resizeRequest);
		return haha.toString();
		
//		return resizeRequest.toString();//		return photoRespository.save(photo);
		
	}
	
	@GetMapping("/prepareUpload")
	public String[] prepareUpload(@RequestParam String ext) {
		S3Utils s3Utils = new S3Utils();
		return s3Utils.getPresignedPost(ext);
		
	}
	

}
