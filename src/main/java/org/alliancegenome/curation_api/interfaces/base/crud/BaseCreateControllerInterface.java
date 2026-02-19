package org.alliancegenome.curation_api.interfaces.base.crud;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
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
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Create entity", description = "Create a new entity from the submitted JSON payload")
	@RequestBody(description = "The entity object to create")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The created entity")
	)
	ObjectResponse<E> create(E entity);

	@POST
	@Path("/multiple")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Create multiple entities", description = "Create multiple entities from the submitted JSON array")
	@RequestBody(description = "List of entity objects to create")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The list of created entities")
	)
	ObjectListResponse<E> create(List<E> entities);

}
