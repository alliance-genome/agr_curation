package org.alliancegenome.curation_api.interfaces.base;

import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

public interface BaseUpsertControllerInterface<E extends AuditedObject, T extends BaseDTO> {

	@POST
	@Path("/upsert")
	@JsonView(CurationView.FieldsOnly.class)
	@Operation(summary = "Upsert entity from DTO", description = "Create or update an entity from a data transfer object. If the entity exists it will be updated, otherwise a new one is created.")
	@RequestBody(description = "The DTO object to upsert")
	@APIResponses(
		@APIResponse(
			responseCode = "200",
			description = "The created or updated entity")
	)
	ObjectResponse<E> upsert(T dto) throws ValidationException;
}
