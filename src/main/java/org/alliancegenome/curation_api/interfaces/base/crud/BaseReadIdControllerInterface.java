package org.alliancegenome.curation_api.interfaces.base.crud;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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
public interface BaseReadIdControllerInterface<E extends AuditedObject> {

	@GET
	@Path("/{id}")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Get entity by ID", description = "Retrieve a single entity by its internal database ID")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The entity matching the given ID")
	)
	ObjectResponse<E> getById(@Parameter(description = "Internal database ID of the entity") @PathParam("id") Long id);

}