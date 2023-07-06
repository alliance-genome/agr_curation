package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SecondaryIdSlotAnnotationDTOValidator;

@RequestScoped
public class GeneSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator {

	public ObjectResponse<GeneSecondaryIdSlotAnnotation> validateGeneSecondaryIdSlotAnnotationDTO(GeneSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		ObjectResponse<GeneSecondaryIdSlotAnnotation> gsidResponse = new ObjectResponse<GeneSecondaryIdSlotAnnotation>();

		if (annotation == null)
			annotation = new GeneSecondaryIdSlotAnnotation();

		ObjectResponse<GeneSecondaryIdSlotAnnotation> saResponse = validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		gsidResponse.addErrorMessages(saResponse.getErrorMessages());

		gsidResponse.setEntity(annotation);

		return gsidResponse;
	}
}
