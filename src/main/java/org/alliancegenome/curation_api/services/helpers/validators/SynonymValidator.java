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

	public ObjectResponse<Synonym> validateSynonym(Synonym uiEntity) {
		Synonym synonym = validateSynonym(uiEntity, false);
		response.setEntity(synonym);
		return response;
	}
	
	public Synonym validateSynonym(Synonym uiEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update Synonym: [" + uiEntity.getName() + "]";

		Long id = uiEntity.getId();
		Synonym dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = synonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find Synonym with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new Synonym();
			newEntity = true;
		}
		
		dbEntity = (Synonym) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);
		
		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			}
			return null;
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