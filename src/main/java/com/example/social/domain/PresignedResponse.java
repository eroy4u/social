package com.example.social.domain;


public class PresignedResponse {

	private String keyName;
	private String presignedUrl;
	
	public PresignedResponse(String keyName, String presignedUrl) {
		super();
		this.keyName = keyName;
		this.presignedUrl = presignedUrl;
	}

}
