package org.alliancegenome.curation_api.interfaces.crud.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.interfaces.base.BaseIdCrudInterface;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/allelegermlinetransmissionstatusslotannotation")
@Tag(name = "CRUD - Allele Germline Transmission Status Slot Annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlleleGermlineTransmissionStatusSlotAnnotationCrudInterface extends BaseIdCrudInterface<AlleleGermlineTransmissionStatusSlotAnnotation> {

	@POST
	@Path("/validate")
	@JsonView(View.FieldsAndLists.class)
	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> validate(AlleleGermlineTransmissionStatusSlotAnnotation entity);
}
