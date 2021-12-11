package com.example.social;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social.security.JwtConfiguration;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

@SpringBootApplication
@RestController
public class SocialApplication {
	
	@Autowired
	private JwtConfiguration jwtConfiguration;
	
	@RequestMapping("/")
	public String home() {
		return "Home page";
	}
	
	

	public static void main(String[] args) {
		SpringApplication.run(SocialApplication.class, args);
	}
	
	@Bean
	public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
	   ResourceRetriever resourceRetriever =
	         new DefaultResourceRetriever(2000,2000);
	   URL jwkURL= new URL("https://cognito-idp.ap-southeast-1.amazonaws.com/ap-southeast-1_5xZ9vJW9l/.well-known/jwks.json");
	   JWKSource keySource= new RemoteJWKSet(jwkURL, resourceRetriever);
	   ConfigurableJWTProcessor jwtProcessor= new DefaultJWTProcessor();
	   JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	   jwtProcessor.setJWSKeySelector(keySelector);
	   return jwtProcessor;
	   
	}

}
