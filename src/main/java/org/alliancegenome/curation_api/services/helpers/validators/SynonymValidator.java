package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class SynonymValidator extends AuditedObjectValidator<Synonym> {

	@Inject
	SynonymDAO synonymDAO;

	public Synonym validateSynonym(Synonym uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Synonym: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Synonym ID provided");
			throw new ApiErrorException(response);
		}
		Synonym dbEntity = synonymDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Synonym with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}
		
		dbEntity = (Synonym) validateAuditedObjectFields(uiEntity, dbEntity, false);
		
		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}
		
		return dbEntity;
	}
	
	public String validateName(Synonym uiEntity) {
		String field = "name";
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		return uiEntity.getName();
	}
}