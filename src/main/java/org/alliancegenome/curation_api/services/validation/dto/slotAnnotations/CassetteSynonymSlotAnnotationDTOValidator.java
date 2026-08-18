package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CassetteSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<CassetteSynonymSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<CassetteSynonymSlotAnnotation> validateCassetteSynonymSlotAnnotationDTO(CassetteSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<CassetteSynonymSlotAnnotation>();

		if (annotation == null) {
			annotation = new CassetteSynonymSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.NAME_TYPE_VOCABULARY);

		response.setEntity(annotation);
		return response;
	}
}
