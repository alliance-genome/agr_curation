package org.alliancegenome.curation_api.interfaces.crud.slotAnnotations;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/genesynonymslotannotation")
@Tag(name = "CRUD - Gene Synonym Slot Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneSynonymSlotAnnotationCrudInterface extends BaseIdCrudInterface<GeneSynonymSlotAnnotation> {

	@Operation(summary = "Validate gene synonym slot annotation", description = "Validate a gene synonym slot annotation entity without persisting it")
	@POST
	@Path("/validate")
	@JsonView(CurationView.FieldsAndLists.class)
	@APIResponses(
		@APIResponse(
			description = "Validate Object",
			content = @Content(
				mediaType = "application/json"
			)
		)
	)
	ObjectResponse<GeneSynonymSlotAnnotation> validate(GeneSynonymSlotAnnotation entity);
}
