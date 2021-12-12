package com.example.social.domain;


public class PresignedResponse {

	private String keyName;
	private String presignedUrl;
	
	public PresignedResponse(String keyName, String presignedUrl) {
		super();
		this.keyName = keyName;
		this.presignedUrl = presignedUrl;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getPresignedUrl() {
		return presignedUrl;
	}

	public void setPresignedUrl(String presignedUrl) {
		this.presignedUrl = presignedUrl;
	}

}
