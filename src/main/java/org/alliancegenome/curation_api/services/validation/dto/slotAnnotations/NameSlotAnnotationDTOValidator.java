package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class NameSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;

	public <E extends NameSlotAnnotation> ObjectResponse<E> validateNameSlotAnnotationDTO(E annotation, NameSlotAnnotationDTO dto) {
		ObjectResponse<E> nsaResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = nsaResponse.getEntity();

		if (StringUtils.isBlank(dto.getDisplayText())) {
			nsaResponse.addErrorMessage("display_text", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setDisplayText(dto.getDisplayText());
		}

		if (StringUtils.isBlank(dto.getFormatText())) {
			nsaResponse.addErrorMessage("format_text", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setFormatText(dto.getFormatText());
		}

		if (StringUtils.isBlank(dto.getSynonymUrl())) {
			annotation.setSynonymUrl(null);
		} else {
			annotation.setSynonymUrl(dto.getSynonymUrl());
		}

		if (StringUtils.isNotBlank(dto.getSynonymScopeName())) {
			VocabularyTerm synonymScope = vocabularyTermService.getTermInVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY, dto.getSynonymScopeName()).getEntity();
			if (synonymScope == null)
				nsaResponse.addErrorMessage("synonym_scope", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSynonymScopeName() + ")");
			annotation.setSynonymScope(synonymScope);
		} else {
			annotation.setSynonymScope(null);
		}

		nsaResponse.setEntity(annotation);

		return nsaResponse;
	}
}
