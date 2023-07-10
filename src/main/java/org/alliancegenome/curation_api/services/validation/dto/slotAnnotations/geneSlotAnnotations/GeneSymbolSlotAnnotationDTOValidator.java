package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<GeneSymbolSlotAnnotation> validateGeneSymbolSlotAnnotationDTO(GeneSymbolSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		ObjectResponse<GeneSymbolSlotAnnotation> gssaResponse = new ObjectResponse<GeneSymbolSlotAnnotation>();

		if (annotation == null)
			annotation = new GeneSymbolSlotAnnotation();

		ObjectResponse<GeneSymbolSlotAnnotation> saResponse = validateNameSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		gssaResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, dto.getNameTypeName()).getEntity();
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
