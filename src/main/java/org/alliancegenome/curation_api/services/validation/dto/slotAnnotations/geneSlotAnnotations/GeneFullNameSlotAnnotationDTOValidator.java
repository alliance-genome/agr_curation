package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {
	
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
	public ObjectResponse<GeneFullNameSlotAnnotation> validateGeneFullNameSlotAnnotationDTO(NameSlotAnnotationDTO dto) {
		ObjectResponse<GeneFullNameSlotAnnotation> gfnsaResponse = new ObjectResponse<GeneFullNameSlotAnnotation>();
		
		GeneFullNameSlotAnnotation annotation = new GeneFullNameSlotAnnotation();
		
		ObjectResponse<GeneFullNameSlotAnnotation> saResponse = validateNameSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		gfnsaResponse.addErrorMessages(saResponse.getErrorMessages());
		
		if (StringUtils.isNotEmpty(dto.getNameTypeName())) {
			VocabularyTerm nameType = vocabularyTermDAO.getTermInVocabularyTermSet(VocabularyConstants.FULL_NAME_TYPE_TERM_SET, dto.getNameTypeName());
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
