package com.example.social.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private AwsCognitoJwtAuthFilter awsCognitoJwtAuthenticationFilter;
	
	@Autowired
	private CorsFilter corsFilter;
	
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.headers().cacheControl();
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("**/health").permitAll()
				.antMatchers("/api/**").authenticated()
				//.anyRequest().authenticated()
				.and()
				.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(awsCognitoJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}

}