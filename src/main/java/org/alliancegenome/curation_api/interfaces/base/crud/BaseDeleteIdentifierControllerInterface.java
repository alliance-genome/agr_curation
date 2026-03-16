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
public interface BaseDeleteIdentifierControllerInterface<E extends AuditedObject> {

	@DELETE
	@Path("/{identifierString}")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Delete entity by identifier", description = "Delete an entity by its identifier string (curie, MOD ID, or other unique identifier)")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The deleted entity")
	)
	ObjectResponse<E> deleteByIdentifier(@Parameter(description = "Identifier string (curie, MOD ID, etc.)") @PathParam("identifierString") String identifierString);

}
