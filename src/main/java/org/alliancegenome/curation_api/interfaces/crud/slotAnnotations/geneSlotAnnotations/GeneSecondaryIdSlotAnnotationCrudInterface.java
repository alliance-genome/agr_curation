package org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.geneSlotAnnotations;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/genesecondaryidslotannotation")
@Tag(name = "CRUD - Gene Secondary ID Slot Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GeneSecondaryIdSlotAnnotationCrudInterface extends BaseIdCrudInterface<GeneSecondaryIdSlotAnnotation> {

	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validate(GeneSecondaryIdSlotAnnotation entity);
}