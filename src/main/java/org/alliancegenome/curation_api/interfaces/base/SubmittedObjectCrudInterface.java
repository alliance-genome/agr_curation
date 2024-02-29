package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.constraints.Null;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubmittedObjectCrudInterface<E extends SubmittedObject> extends CurieObjectCrudInterface<E> {

	@Override
	@GET
	@Path("/{identifierString}")
	@JsonView(View.FieldsOnly.class)
	@APIResponses(
		@APIResponse(
			description = "Get the Entity by Identifier String",
			responseCode = "200",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> get(@PathParam("identifierString") String identifierString);

	@Override
	@DELETE
	@Path("/{identifierString}")
	@JsonView(View.FieldsOnly.class)
	@APIResponses(
		@APIResponse(
			description = "Delete the Entity by Identifier String",
			responseCode = "200",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> delete(@PathParam("identifierString") String identifierString);

}
