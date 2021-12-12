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
import com.example.social.domain.User;
import com.example.social.security.JwtAuthentication;
import com.example.social.utils.S3Utils;

@RestController
public class PhotoController {
	public static final long DAY_IN_MS = 1000 * 60 * 60 * 24;
	public static final long EXPIRED_DAYS = 7;
	
	@Autowired
	private PhotoRepository photoRepository;
	
	@GetMapping("/photo")
	public Iterable<Photo> all(){
		String userId = getUser().getId();
		return photoRepository.findByUserIdAndExpiredAtGreaterThanOrderByCreatedAtDesc(userId, new Date());
	}
	
	private User getUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		User user = (principal instanceof User) ? (User) principal : null;
		return user;
	}
	
	@PostMapping("/resize")	
	public Photo resize(@RequestBody ResizeRequest resizeRequest) throws IOException {
		//1. download photo from s3 using the keyName
		//2. Resize to thumbnail size and put it back to s3 with thumbnail prefix
		//3. Resize per width/height or ratio and put it back to s3 with resized prefix
		//4. get presigned get URL with 7 days duration
		//5. save as new Photo in database
		
		String filename = resizeRequest.getKeyName().split("/")[1];
		
		//1. download photo from s3 using the keyName
		S3Utils s3Utils = new S3Utils();
		BufferedImage source = s3Utils.downloadImage(resizeRequest.getKeyName());
		
		//2. Resize to thumbnail size and put it back to s3 with thumbnail prefix
		BufferedImage thumbnail = Scalr.resize(source, 100);
		String thumbnailKey = "thumbnail/"+filename;
		s3Utils.uploadImage(thumbnail, thumbnailKey);
		
		//3. Resize per width/height or ratio and put it back to s3 with resized prefix
		int targetWidth = source.getWidth();
		int targetHeight = source.getHeight();
		//either a ratio is specified, or width/height are specified
		if (resizeRequest.getRatio() > 0) {
			targetWidth = (int) (targetWidth * resizeRequest.getRatio());
			targetHeight = (int) (targetWidth * resizeRequest.getRatio());
		}else {
			if (resizeRequest.getWidth() >0 ) {
				//resize by width
				targetWidth = resizeRequest.getWidth();
			}
			if (resizeRequest.getHeight() > 0) {
				targetHeight = resizeRequest.getHeight();
			}	
		}
		
		BufferedImage resized = Scalr.resize(source, targetWidth, targetHeight);
		String resizedKey = "resized/"+filename;
		s3Utils.uploadImage(resized, resizedKey);
		
		//4. get presigned url with 7 days duration
		Photo photo = new Photo();
		photo.setCreatedAt(new Date());
		
		//5. save as new Photo in database
		Date expired = new Date(System.currentTimeMillis() + EXPIRED_DAYS * DAY_IN_MS);
		photo.setExpiredAt(expired);
		photo.setResizedPath(s3Utils.getPresignedGet(resizedKey, EXPIRED_DAYS));
		photo.setThumbnailPath(s3Utils.getPresignedGet(thumbnailKey, EXPIRED_DAYS));
		photo.setUserId(getUser().getId());
		
		return photoRepository.save(photo);
		
	}
	
	@GetMapping("/prepareUpload")
	@ResponseBody
	public PresignedResponse prepareUpload(@RequestParam String ext) {
		S3Utils s3Utils = new S3Utils();
		String[] result= s3Utils.getPresignedPut(ext);
		return new PresignedResponse(result[0], result[1]);
		
	}
	

}
