package org.alliancegenome.curation_api.auth;

import java.net.URL;
import java.text.ParseException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AuthenticationService {

	@ConfigProperty(name = "cognito.user.pool.id")
	Instance<String> userPoolId;

	@ConfigProperty(name = "cognito.region")
	Instance<String> region;

	private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
	private String issuer;

	@PostConstruct
	public void init() {
		try {
			// Cognito issuer format: https://cognito-idp.{region}.amazonaws.com/{userPoolId}
			issuer = String.format("https://cognito-idp.%s.amazonaws.com/%s", region.get(), userPoolId.get());

			// JWK set URL for Cognito
			String jwkSetURL = issuer + "/.well-known/jwks.json";

			// Create JWK source from Cognito's JWKS endpoint
			JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(jwkSetURL));

			// Create JWT processor
			jwtProcessor = new DefaultJWTProcessor<>();

			// Set JWS key selector (Cognito uses RS256)
			JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
			JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
			jwtProcessor.setJWSKeySelector(keySelector);

			log.info("Cognito JWT verifier initialized with issuer: " + issuer);
		} catch (Exception e) {
			log.error("Failed to initialize Cognito JWT verifier", e);
			throw new RuntimeException("Failed to initialize Cognito JWT verifier", e);
		}
	}

	public JWTClaimsSet verifyToken(String token) throws ParseException, BadJOSEException, JOSEException {
		// Process the token
		SecurityContext context = null; // Optional context parameter, can be null
		JWTClaimsSet claimsSet = jwtProcessor.process(token, context);

		// Verify issuer
		String tokenIssuer = claimsSet.getIssuer();
		if (!issuer.equals(tokenIssuer)) {
			throw new BadJOSEException("Invalid issuer: " + tokenIssuer);
		}

		return claimsSet;
	}

	public String extractUserId(String token) throws ParseException, BadJOSEException, JOSEException {
		JWTClaimsSet claims = verifyToken(token);
		return claims.getSubject(); // 'sub' claim contains the Cognito user ID
	}

	public String extractClientId(String token) throws ParseException {
		// For client credentials flow, parse without verification to get client_id
		JWT jwt = JWTParser.parse(token);
		JWTClaimsSet claims = jwt.getJWTClaimsSet();
		return claims.getStringClaim("client_id");
	}
}
