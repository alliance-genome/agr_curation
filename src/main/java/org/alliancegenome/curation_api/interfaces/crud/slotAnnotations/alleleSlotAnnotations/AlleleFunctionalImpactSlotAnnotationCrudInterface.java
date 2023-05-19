package org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/allelefunctionalimpactslotannotation")
@Tag(name = "CRUD - Allele Functional Impact Slot Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleFunctionalImpactSlotAnnotationCrudInterface extends BaseIdCrudInterface<AlleleFunctionalImpactSlotAnnotation> {

	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> validate(AlleleFunctionalImpactSlotAnnotation entity);
}
