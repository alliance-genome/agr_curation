package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<GeneFullNameSlotAnnotation> validateGeneFullNameSlotAnnotationDTO(GeneFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		ObjectResponse<GeneFullNameSlotAnnotation> gfnsaResponse = new ObjectResponse<GeneFullNameSlotAnnotation>();

		if (annotation == null)
			annotation = new GeneFullNameSlotAnnotation();

		ObjectResponse<GeneFullNameSlotAnnotation> saResponse = validateNameSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		gfnsaResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.FULL_NAME_TYPE_TERM_SET, dto.getNameTypeName()).getEntity();
			if (nameType == null)
				gfnsaResponse.addErrorMessage("name_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNameTypeName() + ")");
			annotation.setNameType(nameType);
		} else {
			gfnsaResponse.addErrorMessage("name_type_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		gfnsaResponse.setEntity(annotation);

		return gfnsaResponse;
	}
}
