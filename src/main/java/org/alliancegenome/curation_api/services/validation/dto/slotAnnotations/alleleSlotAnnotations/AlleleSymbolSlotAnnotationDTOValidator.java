package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {
    
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
	public ObjectResponse<AlleleSymbolSlotAnnotation> validateAlleleSymbolSlotAnnotationDTO(NameSlotAnnotationDTO dto) {
    	ObjectResponse<AlleleSymbolSlotAnnotation> assaResponse = new ObjectResponse<AlleleSymbolSlotAnnotation>();
    	
    	AlleleSymbolSlotAnnotation annotation = new AlleleSymbolSlotAnnotation();
    	
    	ObjectResponse<AlleleSymbolSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
    	annotation = saResponse.getEntity();
    	assaResponse.addErrorMessages(saResponse.getErrorMessages());
    	
    	if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermDAO.getTermInVocabularyTermSet(VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, dto.getNameTypeName());
			if (nameType == null)
				assaResponse.addErrorMessage("name_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNameTypeName() + ")");
			annotation.setNameType(nameType);
		} else {
			assaResponse.addErrorMessage("name_type_name", ValidationConstants.REQUIRED_MESSAGE);
		}
    	
    	assaResponse.setEntity(annotation);
    	
    	return assaResponse;
    }
}
