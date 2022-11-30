package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {
    
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
	public ObjectResponse<AlleleSynonymSlotAnnotation> validateAlleleSynonymSlotAnnotationDTO(NameSlotAnnotationDTO dto) {
    	ObjectResponse<AlleleSynonymSlotAnnotation> assaResposne = new ObjectResponse<AlleleSynonymSlotAnnotation>();
    	
    	AlleleSynonymSlotAnnotation annotation = new AlleleSynonymSlotAnnotation();
    	
    	ObjectResponse<AlleleSynonymSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
    	annotation = saResponse.getEntity();
    	assaResposne.addErrorMessages(saResponse.getErrorMessages());
    	
    	if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY, dto.getNameTypeName());
			if (nameType == null)
				assaResposne.addErrorMessage("name_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNameTypeName() + ")");
			annotation.setNameType(nameType);
		} else {
			assaResposne.addErrorMessage("name_type_name", ValidationConstants.REQUIRED_MESSAGE);
		}
    	
    	assaResposne.setEntity(annotation);
    	
    	return assaResposne;
    }
}
