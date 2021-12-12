package com.example.social.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.UUID;

import javax.imageio.ImageIO;

public class S3Utils {

	private String bucketName = "social-upload-demo";
	private String uploadPath = "tmp";
	private Region region = Region.AP_SOUTHEAST_1;
	private S3Client s3;

	public S3Utils() {

		s3 = S3Client.builder().region(region).build();
	}

	private String uuidFilename(String extension) {
		return uploadPath + "/" + UUID.randomUUID() + "." + extension.toLowerCase();
	}

	/**
	 * Get Presigned URL for upload
	 * 
	 * @param extension
	 * @return
	 */
	public String[] getPresignedPut(String extension) {
		S3Presigner presigner = S3Presigner.create();
		String keyName = uuidFilename(extension);

		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(20)).putObjectRequest(objectRequest).build();

		PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

		return new String[] { keyName, presignedRequest.url().toString() };
	}

	/**
	 * Get the Presigned Get URL for downloading the image
	 * 
	 * @param keyName
	 * @return
	 */
	public String getPresignedGet(String keyName, long days) {
		S3Presigner presigner = S3Presigner.create();
		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(keyName).build();
		GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofDays(days)).getObjectRequest(getObjectRequest).build();
		// Generate the presigned request
		PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
		return presignedGetObjectRequest.url().toString();

	}

	/**
	 * Download image form S3 as BufferedImage
	 * 
	 * @param keyName
	 * @return
	 * @throws IOException
	 */
	public BufferedImage downloadImage(String keyName) throws IOException {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(keyName).build();

		ResponseInputStream<GetObjectResponse> stream = s3.getObject(getObjectRequest);
		return ImageIO.read(stream);
	}

	private ByteBuffer getBufferFromImage(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		ByteBuffer buf = ByteBuffer.wrap(imageInByte);
		return buf;
	}

	/**
	 * Upload image to S3
	 * 
	 * @param image
	 * @param keyName
	 * @return
	 * @throws IOException
	 */
	public String uploadImage(BufferedImage image, String keyName) throws IOException {

		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();
		ByteBuffer buffer = this.getBufferFromImage(image);
		s3.putObject(objectRequest, RequestBody.fromByteBuffer(buffer));
		return keyName;
	}

}
