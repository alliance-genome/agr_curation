package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class NameSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	VocabularyTermDAO vocabularyTermDAO;

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
			VocabularyTerm synonymScope = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY, dto.getSynonymScopeName());
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
