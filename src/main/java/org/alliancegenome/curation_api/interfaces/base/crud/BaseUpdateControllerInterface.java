package org.alliancegenome.curation_api.interfaces.base.crud;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseUpdateControllerInterface<E extends AuditedObject> {

	@PUT
	@Path("/")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Update entity", description = "Update an existing entity with the submitted JSON payload")
	@RequestBody(description = "The entity object with updated fields")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The updated entity")
	)
	ObjectResponse<E> update(E entity);

}
