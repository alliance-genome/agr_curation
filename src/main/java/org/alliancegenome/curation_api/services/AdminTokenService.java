package org.alliancegenome.curation_api.services;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class AdminTokenService {

	@Inject
	Instance<OidcClient> oidcClient;

	private Tokens tokens;

	public synchronized String getAdminAccessToken() {
		if (!oidcClient.isResolvable()) {
			throw new IllegalStateException("OIDC client is not enabled; cannot generate an admin token");
		}
		if (tokens == null || tokens.isAccessTokenExpired()) {
			tokens = oidcClient.get().getTokens().await().indefinitely();
		}
		return tokens.getAccessToken();
	}
}
