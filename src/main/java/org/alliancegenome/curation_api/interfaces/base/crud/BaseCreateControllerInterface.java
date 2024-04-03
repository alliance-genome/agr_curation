package org.alliancegenome.curation_api.interfaces.base.crud;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseCreateControllerInterface<E extends AuditedObject> {

	@POST
	@Path("/")
	@JsonView(View.FieldsOnly.class)
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectResponse<E> create(E entity);

	@POST
	@Path("/multiple")
	@JsonView(View.FieldsOnly.class)
	@RequestBody( 
		description = "Post Request",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = String.class)
		)
	)
	@APIResponses(
		@APIResponse(
			description = "Response Entity",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Null.class)
			)
		)
	)
	public ObjectListResponse<E> create(List<E> entities);
	
}
