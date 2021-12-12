package com.example.social.domain;

import org.springframework.stereotype.Component;



public class ResizeRequest {
	
	public ResizeRequest() {
		
	}
	private String keyName;
	private int width;
	private int height;
	private float ratio;
	
	@Override
	public String toString() {
		return "RequestRequest: keyName="+keyName+", width="+width+", height="+height+", ratio="+ratio;
		
	}
	public ResizeRequest(String keyName, int width, int height, float ratio) {
		super();
		this.keyName = keyName;
		this.width = width;
		this.height = height;
		
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public float getRatio() {
		return ratio;
	}
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	
	

}
