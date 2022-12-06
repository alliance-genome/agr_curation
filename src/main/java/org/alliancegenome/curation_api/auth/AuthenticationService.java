package org.alliancegenome.curation_api.auth;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.jwt.JwtVerifiers;

@ApplicationScoped
public class AuthenticationService {
	
	@ConfigProperty(name = "okta.url")
	Instance<String> okta_url;
	
	private AccessTokenVerifier jwtVerifier;
	
	@PostConstruct
	public void init() {
		jwtVerifier = JwtVerifiers.accessTokenVerifierBuilder()
					.setIssuer(okta_url.get() + "/oauth2/default")
					.setAudience("api://default")
					.setConnectionTimeout(Duration.ofSeconds(1))
					//.setReadTimeout(Duration.ofSeconds(1))
					.build();
	}
	
	public Jwt verifyToken(String token) throws JwtVerificationException {
		return jwtVerifier.decode(token);
	}
}
