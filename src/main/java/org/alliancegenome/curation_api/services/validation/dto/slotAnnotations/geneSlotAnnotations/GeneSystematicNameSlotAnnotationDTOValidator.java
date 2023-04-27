package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneSystematicNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<GeneSystematicNameSlotAnnotation> validateGeneSystematicNameSlotAnnotationDTO(GeneSystematicNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		ObjectResponse<GeneSystematicNameSlotAnnotation> gsnsaResponse = new ObjectResponse<GeneSystematicNameSlotAnnotation>();

		if (annotation == null)
			annotation = new GeneSystematicNameSlotAnnotation();

		ObjectResponse<GeneSystematicNameSlotAnnotation> saResponse = validateNameSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		gsnsaResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.SYSTEMATIC_NAME_TYPE_TERM_SET, dto.getNameTypeName()).getEntity();
			if (nameType == null)
				gsnsaResponse.addErrorMessage("name_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNameTypeName() + ")");
			annotation.setNameType(nameType);
		} else {
			gsnsaResponse.addErrorMessage("name_type_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		gsnsaResponse.setEntity(annotation);

		return gsnsaResponse;
	}
}
