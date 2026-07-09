package org.alliancegenome.curation_api.services;

import io.quarkus.oidc.client.Tokens;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AdminTokenService {

	@Inject
	Tokens tokens;

	public String getAdminAccessToken() {
		return tokens.getAccessToken();
	}
}
