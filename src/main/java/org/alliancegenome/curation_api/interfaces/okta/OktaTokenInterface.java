package org.alliancegenome.curation_api.interfaces.okta;

import javax.ws.rs.*;

@Path("/oauth2/default/v1")
public interface OktaTokenInterface {

    @POST
    @Path("/introspect")
    @Produces({"application/json"})
    public OktaUserInfo getUserInfo(
        @HeaderParam("Authorization") String authorization,
        //@FormParam("client_id") String client_id,
        @FormParam("token_type_hint") String token_type_hint,
        @FormParam("token") String token
    );
}
