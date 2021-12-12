package com.example.social;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.social.domain.Photo;
import com.example.social.domain.PhotoRepository;
import com.example.social.domain.PresignedResponse;
import com.example.social.domain.ResizeRequest;
import com.example.social.utils.S3Utils;

@RestController
public class PhotoController {
	public static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
	public static final long EXPIRED_DAYS = 7;
	
	@Autowired
	private PhotoRepository photoRespository;
	
	@GetMapping("/photo")
	public Iterable<Photo> all(){
		return photoRespository.findAll();
	}
	
	@GetMapping("/test")
	public String test() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		System.out.println(principal);
		System.out.println(authentication.getDetails());
		return "abc";
	}
	
	@PostMapping("/resize")	
	public String resize(@RequestBody ResizeRequest resizeRequest) throws IOException {
		//1. take photo from s3 using the keyName
		//2. Resize to thumbnail size and put it back to s3 with thumbnail prefix
		//3. Resize per width/height or ratio and put it back to s3 with resized prefix
		//4. get presigned get URL with 7 days duration
		//5. save to Photo
		
		String filename = resizeRequest.getKeyName().split("/")[1];
		
		S3Utils s3Utils = new S3Utils();
		BufferedImage source = s3Utils.downloadImage(resizeRequest.getKeyName());
		
		BufferedImage thumbnail = Scalr.resize(source, 100);
		String thumbnailKey = "thumbnail/"+filename;
		s3Utils.uploadImage(thumbnail, thumbnailKey);
		
		BufferedImage resized = Scalr.resize(source, 200);
		String resizedKey = "resized/"+filename;
		s3Utils.uploadImage(resized, resizedKey);
		
		//4. get presigned url with 7 days duration
		Photo photo = new Photo();
		photo.setCreatedAt(new Date());
		
		Date expired = new Date(System.currentTimeMillis() + EXPIRED_DAYS * DAY_IN_MS);
		photo.setExpiredAt(expired);
		photo.setResizedPath(s3Utils.getPresignedGet(resizedKey, EXPIRED_DAYS));
		photo.setThumbnailPath(s3Utils.getPresignedGet(thumbnailKey, EXPIRED_DAYS));
		
		
		
		
		
		return resizeRequest.toString();
		
//		return resizeRequest.toString();//		return photoRespository.save(photo);
		
	}
	
	@GetMapping("/prepareUpload")
	@ResponseBody
	public PresignedResponse prepareUpload(@RequestParam String ext) {
		S3Utils s3Utils = new S3Utils();
		String[] result= s3Utils.getPresignedPut(ext);
		return new PresignedResponse(result[0], result[1]);
		
	}
	

}
