package com.example.social.security;

import com.example.social.domain.User;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final Object principal;
    private JWTClaimsSet jwtClaimsSet;

    public JwtAuthentication(Object principal, JWTClaimsSet jwtClaimsSet, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.jwtClaimsSet = jwtClaimsSet;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public JWTClaimsSet getJwtClaimsSet() {
        return this.jwtClaimsSet;
    }
    
    public User getUser() {
    	if (principal != null && principal instanceof User) {
    		return (User) this.principal;
    	}
    	return null;
    }
}
