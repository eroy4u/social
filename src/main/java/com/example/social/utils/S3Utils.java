package com.example.social.utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;
import java.util.UUID;

public class S3Utils {

	private String bucketName = "social-upload-demo";
	private String uploadPath = "tmp";
	private Region region = Region.AP_SOUTHEAST_1;

	public S3Utils() {

		S3Client s3 = S3Client.builder().region(region).build();
	}

	private String uuidFilename(String extension) {
		return uploadPath + "/" +UUID.randomUUID() + "." + extension.toLowerCase();
	}

	public String[] getPresignedPost(String extension) {
		S3Presigner presigner = S3Presigner.create();
		String keyName = uuidFilename(extension);

		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName)
				.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(20)).putObjectRequest(objectRequest).build();

		PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

		return new String[] {keyName, presignedRequest.url().toString()};

	}
}
