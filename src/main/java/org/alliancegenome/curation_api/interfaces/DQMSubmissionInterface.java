package org.alliancegenome.curation_api.interfaces;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/data")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
public interface DQMSubmissionInterface {

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String update(MultipartFormDataInput input, 
		@DefaultValue("true")
		@QueryParam("cleanUp") Boolean cleanUp);

}