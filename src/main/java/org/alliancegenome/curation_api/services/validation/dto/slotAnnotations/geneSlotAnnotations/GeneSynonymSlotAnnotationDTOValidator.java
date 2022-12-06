package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {
    
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
	public ObjectResponse<GeneSynonymSlotAnnotation> validateGeneSynonymSlotAnnotationDTO(NameSlotAnnotationDTO dto) {
    	ObjectResponse<GeneSynonymSlotAnnotation> gssaResponse = new ObjectResponse<GeneSynonymSlotAnnotation>();
    	
    	GeneSynonymSlotAnnotation annotation = new GeneSynonymSlotAnnotation();
    	
    	ObjectResponse<GeneSynonymSlotAnnotation> saResponse = validateNameSlotAnnotationDTO(annotation, dto);
    	annotation = saResponse.getEntity();
    	gssaResponse.addErrorMessages(saResponse.getErrorMessages());
    	
    	if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY, dto.getNameTypeName());
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
