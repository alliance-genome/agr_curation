package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceValidator extends AuditedObjectValidator<CrossReference> {

	@Inject CrossReferenceDAO crossReferenceDAO;

	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError) {
		return validateCrossReference(uiEntity, throwError, true);
	}

	/**
	 * @param persist whether to write the validated cross reference. Pass false to check a cross reference
	 *        without storing it; the returned entity is then unmanaged and carries no id.
	 */
	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError, Boolean persist) {
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

		if (StringUtils.isEmpty(uiEntity.getReferencedCurie())) {
			addMessageResponse("referencedCurie", ValidationConstants.REQUIRED_MESSAGE);
		}
		dbEntity.setReferencedCurie(uiEntity.getReferencedCurie());

		if (StringUtils.isEmpty(uiEntity.getDisplayName())) {
			if (StringUtils.isEmpty(uiEntity.getReferencedCurie())) {
				addMessageResponse("displayName", ValidationConstants.REQUIRED_MESSAGE);
			}
		}
		dbEntity.setDisplayName(uiEntity.getDisplayName());

		if (uiEntity.getResourceDescriptorPage() != null) {
			dbEntity.setResourceDescriptorPage(uiEntity.getResourceDescriptorPage());
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			}
			return response;
		}

		response.setEntity(persist ? crossReferenceDAO.persist(dbEntity) : dbEntity);

		return response;
	}
}