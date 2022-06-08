package org.alliancegenome.curation_api.interfaces;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/data")
@Tag(name = "Bulk Import")
@Produces(MediaType.APPLICATION_JSON)
public interface DQMSubmissionInterface {

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String update(MultipartFormDataInput input);
	
}