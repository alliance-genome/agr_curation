package org.alliancegenome.curation_api.interfaces.crud;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cross-reference")
@Tag(name = "CRUD - Cross References")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CrossReferenceCrudInterface extends BaseIdCrudInterface<CrossReference> {

	@Operation(summary = "Validate cross reference", description = "Validate a cross reference entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsOnly.class)
	ObjectResponse<CrossReference> validate(CrossReference entity);
}