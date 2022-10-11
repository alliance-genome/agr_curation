package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.dto.SynonymDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class SynonymDTOValidator {
	
	@Inject AuditedObjectDTOValidator<Synonym, SynonymDTO> auditedObjectDtoValidator;
	
	public ObjectResponse<Synonym> validateSynonymDTO(SynonymDTO dto) {
		Synonym synonym = new Synonym();
		ObjectResponse<Synonym> synonymResponse = auditedObjectDtoValidator.validateAuditedObjectDTO(synonym, dto);
		synonym = synonymResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getName())) {
			synonymResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			synonym.setName(dto.getName());
		}
		
		synonymResponse.setEntity(synonym);
		
		return synonymResponse;
	}

}
