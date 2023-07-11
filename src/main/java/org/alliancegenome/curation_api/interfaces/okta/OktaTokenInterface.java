package org.alliancegenome.curation_api.interfaces.okta;

import org.alliancegenome.curation_api.model.okta.OktaToken;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/oauth2/default/v1")
public interface OktaTokenInterface {

	@POST
	@Path("/token")
	public OktaToken getClientCredentialsAccessToken(@HeaderParam("Authorization") String authorization, @FormParam("grant_type") String grantType, @FormParam("scope") String scope);
}