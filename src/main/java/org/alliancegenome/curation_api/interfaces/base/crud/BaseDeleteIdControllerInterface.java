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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BaseDeleteIdControllerInterface<E extends AuditedObject> {

	@DELETE
	@Path("/{id}")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Delete entity by ID", description = "Delete an entity by its internal database ID")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The deleted entity")
	)
	ObjectResponse<E> deleteById(@Parameter(description = "Internal database ID of the entity to delete") @PathParam("id") Long id);

}