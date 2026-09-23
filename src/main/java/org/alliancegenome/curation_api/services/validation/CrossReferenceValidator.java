package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceValidator extends AuditedObjectValidator<CrossReference> {

	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject ResourceDescriptorPageDAO resourceDescriptorPageDAO;

	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError) {
		return validateCrossReference(uiEntity, throwError, true);
	}

	/**
	 * Validates each cross reference in turn, recording an entry's errors against targetResponse under
	 * fieldName and that entry's index.
	 *
	 * @return the validated cross references, or null when any entry failed
	 */
	public List<CrossReference> validateCrossReferences(List<CrossReference> uiXrefs, String fieldName, ObjectResponse<?> targetResponse) {
		List<CrossReference> validatedXrefs = new ArrayList<>();
		boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiXrefs)) {
			for (int ix = 0; ix < uiXrefs.size(); ix++) {
				ObjectResponse<CrossReference> xrefResponse = validateCrossReference(uiXrefs.get(ix), false);
				if (xrefResponse.hasErrors()) {
					allValid = false;
					targetResponse.addErrorMessages(fieldName, ix, xrefResponse.getErrorMessages());
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			targetResponse.convertMapToErrorMessages(fieldName);
			return null;
		}

		return validatedXrefs;
	}

	/**
	 * @param persist whether to write the validated cross reference. Pass false to check a cross reference
	 *        without storing it. A payload with no id is then returned unmanaged and without one; a payload
	 *        carrying an id returns the managed row it names, with the payload's values applied but not
	 *        written, so the caller must not run inside a transaction that would flush them.
	 */
	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity, Boolean throwError, Boolean persist) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CrossReference: [" + uiEntity.getReferencedCurie() + "]";

		CrossReference dbEntity;

		Boolean newEntity = true;
		if (uiEntity.getId() != null) {
			dbEntity = crossReferenceDAO.find(uiEntity.getId());
			if (dbEntity == null) {
				addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
				if (throwError) {
					response.setErrorMessage(errorTitle);
					throw new ApiErrorException(response);
				}
				return response;
			}
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

		// Applied whether or not the payload names one, as the other fields are, so a cleared page is
		// stored as cleared. Resolved rather than taken from the payload when it does name one: the
		// association has no cascade, so an unresolved page would only fail once the transaction flushed,
		// past the point an error response can be built.
		if (uiEntity.getResourceDescriptorPage() == null) {
			dbEntity.setResourceDescriptorPage(null);
		} else {
			ResourceDescriptorPage resourceDescriptorPage = validateEntity(resourceDescriptorPageDAO, "resourceDescriptorPage",
				uiEntity.getResourceDescriptorPage(), dbEntity.getResourceDescriptorPage(), false);
			if (resourceDescriptorPage != null) {
				dbEntity.setResourceDescriptorPage(resourceDescriptorPage);
			}
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