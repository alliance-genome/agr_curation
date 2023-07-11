package org.alliancegenome.curation_api.interfaces;

import org.alliancegenome.curation_api.model.output.APIVersionInfo;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/version")
@Tag(name = "API Version")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface APIVersionInterface {

	@GET
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	public APIVersionInfo get();

}
