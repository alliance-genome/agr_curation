package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {
    
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
	public ObjectResponse<AlleleFullNameSlotAnnotation> validateAlleleFullNameSlotAnnotationDTO(NameSlotAnnotationDTO dto) {
    	ObjectResponse<AlleleFullNameSlotAnnotation> afnsaResponse = new ObjectResponse<AlleleFullNameSlotAnnotation>();
    	
    	AlleleFullNameSlotAnnotation annotation = new AlleleFullNameSlotAnnotation();
    	
    	ObjectResponse<AlleleFullNameSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
    	annotation = saResponse.getEntity();
    	afnsaResponse.addErrorMessages(saResponse.getErrorMessages());
    	
    	if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermDAO.getTermInVocabularyTermSet(VocabularyConstants.FULL_NAME_TYPE_TERM_SET, dto.getNameTypeName());
			if (nameType == null)
				afnsaResponse.addErrorMessage("name_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNameTypeName() + ")");
			annotation.setNameType(nameType);
		} else {
			afnsaResponse.addErrorMessage("name_type_name", ValidationConstants.REQUIRED_MESSAGE);
		}
    	
    	afnsaResponse.setEntity(annotation);
    	
    	return afnsaResponse;
    }
}
