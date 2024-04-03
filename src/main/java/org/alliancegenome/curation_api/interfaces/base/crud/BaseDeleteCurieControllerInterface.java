package org.alliancegenome.curation_api.interfaces.base.crud;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseDeleteCurieControllerInterface<E extends AuditedObject> {

	@GET
	@Path("/{curie}")
	@JsonView(View.FieldsOnly.class)
	@APIResponses(
		@APIResponse(
			description = "Get the Entity by Curie",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> getByCurie(@PathParam("curie") String curie);

}