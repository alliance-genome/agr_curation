package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<GeneSynonymSlotAnnotation> validateGeneSynonymSlotAnnotationDTO(GeneSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		ObjectResponse<GeneSynonymSlotAnnotation> gssaResponse = new ObjectResponse<GeneSynonymSlotAnnotation>();

		if (annotation == null)
			annotation = new GeneSynonymSlotAnnotation();

		ObjectResponse<GeneSynonymSlotAnnotation> saResponse = validateNameSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		gssaResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermService.getTermInVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY, dto.getNameTypeName()).getEntity();
			if (nameType == null)
				gssaResponse.addErrorMessage("name_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNameTypeName() + ")");
			annotation.setNameType(nameType);
		} else {
			gssaResponse.addErrorMessage("name_type_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		gssaResponse.setEntity(annotation);

		return gssaResponse;
	}
}
