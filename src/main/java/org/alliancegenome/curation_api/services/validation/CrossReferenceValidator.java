package org.alliancegenome.curation_api.services.validation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class CrossReferenceValidator extends AuditedObjectValidator<CrossReference> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;

	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CrossReference: [" + uiEntity.getReferencedCurie() + "]";

		CrossReference dbEntity;

		Boolean newEntity = true; 
		if (uiEntity.getId() != null) {
			dbEntity = crossReferenceDAO.find(uiEntity.getId());
			newEntity = false;
		} else {
			dbEntity = new CrossReference();
		}
		
		dbEntity = (CrossReference) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		if (StringUtils.isEmpty(uiEntity.getReferencedCurie()))
			addMessageResponse("referencedCurie", ValidationConstants.REQUIRED_MESSAGE);
		dbEntity.setReferencedCurie(uiEntity.getReferencedCurie());

		if (StringUtils.isEmpty(uiEntity.getDisplayName()))
			if (StringUtils.isEmpty(uiEntity.getReferencedCurie()))
				addMessageResponse("displayName", ValidationConstants.REQUIRED_MESSAGE);
		dbEntity.setDisplayName(uiEntity.getDisplayName());
		
		if (uiEntity.getResourceDescriptorPage() != null)
			dbEntity.setResourceDescriptorPage(uiEntity.getResourceDescriptorPage());
			
		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			}
			return response;
		}
		
		response.setEntity(dbEntity);

		return response;
	}
}