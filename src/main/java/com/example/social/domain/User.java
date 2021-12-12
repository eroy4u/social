package com.example.social.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class User extends org.springframework.security.core.userdetails.User {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.id = id;
	}

}
