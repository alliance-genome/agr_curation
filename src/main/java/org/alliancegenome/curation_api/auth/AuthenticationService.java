package org.alliancegenome.curation_api.auth;

import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
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

	@ConfigProperty(name = "cognito.client.id")
	Instance<String> clientId;

	private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
	private String issuer;
	private Set<String> expectedAudiences;

	@PostConstruct
	public void init() {
		try {
			// Cognito issuer format: https://cognito-idp.{region}.amazonaws.com/{userPoolId}
			issuer = String.format("https://cognito-idp.%s.amazonaws.com/%s", region.get(), userPoolId.get());

			// Expected audiences for Cognito tokens
			expectedAudiences = new HashSet<>(Arrays.asList(
				clientId.get(),           // App client ID
				"api://default"           // Legacy compatibility
			));

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
		// Process and verify the token signature
		SecurityContext context = null; // Optional context parameter, can be null
		JWTClaimsSet claimsSet = jwtProcessor.process(token, context);

		// Verify issuer
		String tokenIssuer = claimsSet.getIssuer();
		if (!issuer.equals(tokenIssuer)) {
			throw new BadJOSEException("Invalid issuer: " + tokenIssuer);
		}

		// Verify audience (aud claim)
		if (claimsSet.getAudience() != null && !claimsSet.getAudience().isEmpty()) {
			boolean validAudience = claimsSet.getAudience().stream()
				.anyMatch(expectedAudiences::contains);
			if (!validAudience) {
				throw new BadJOSEException("Invalid audience: " + claimsSet.getAudience());
			}
		}

		// Verify token_use claim (should be "access" or "id" for user tokens)
		String tokenUse = claimsSet.getStringClaim("token_use");
		if (tokenUse == null) {
			throw new BadJOSEException("Missing token_use claim");
		}

		return claimsSet;
	}

	public JWTClaimsSet verifyUserToken(String token) throws ParseException, BadJOSEException, JOSEException {
		JWTClaimsSet claims = verifyToken(token);

		// For user tokens, token_use should be "access" or "id"
		String tokenUse = claims.getStringClaim("token_use");
		if (!"access".equals(tokenUse) && !"id".equals(tokenUse)) {
			throw new BadJOSEException("Invalid token_use for user token: " + tokenUse);
		}

		// User tokens must have a 'sub' claim
		if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
			throw new BadJOSEException("Missing sub claim in user token");
		}

		return claims;
	}

	public JWTClaimsSet verifyClientCredentialsToken(String token) throws ParseException, BadJOSEException, JOSEException {
		JWTClaimsSet claims = verifyToken(token);

		// For client credentials flow, verify this is NOT a user token
		// Client credentials tokens should NOT have a 'sub' claim (or it should be empty)
		String sub = claims.getSubject();
		if (sub != null && !sub.isEmpty()) {
			throw new BadJOSEException("Client credentials token should not have sub claim");
		}

		// Client credentials tokens must have a 'client_id' claim
		String clientIdClaim = claims.getStringClaim("client_id");
		if (clientIdClaim == null || clientIdClaim.isEmpty()) {
			throw new BadJOSEException("Missing client_id claim in client credentials token");
		}

		return claims;
	}

	public String extractUserId(String token) throws ParseException, BadJOSEException, JOSEException {
		JWTClaimsSet claims = verifyUserToken(token);
		return claims.getSubject(); // 'sub' claim contains the Cognito user ID
	}

	public String extractClientId(String token) throws ParseException, BadJOSEException, JOSEException {
		// SECURITY FIX: Verify the token signature before trusting client_id claim
		JWTClaimsSet claims = verifyClientCredentialsToken(token);
		return claims.getStringClaim("client_id");
	}
}
