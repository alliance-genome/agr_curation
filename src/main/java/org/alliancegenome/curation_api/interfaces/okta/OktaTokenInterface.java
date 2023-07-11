package org.alliancegenome.curation_api.interfaces.okta;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.alliancegenome.curation_api.model.okta.OktaToken;

@Path("/oauth2/default/v1")
public interface OktaTokenInterface {

	@POST
	@Path("/token")
	public OktaToken getClientCredentialsAccessToken(@HeaderParam("Authorization") String authorization, @FormParam("grant_type") String grantType, @FormParam("scope") String scope);
}
