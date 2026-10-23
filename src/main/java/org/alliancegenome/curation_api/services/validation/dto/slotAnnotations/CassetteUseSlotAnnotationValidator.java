package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.ontology.FbcvTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: mirrors AlleleMutationTypeSlotAnnotationValidator, the closest existing validator for
 * a slot annotation whose payload is a required list of ontology terms.
 */
@RequestScoped
public class CassetteUseSlotAnnotationValidator extends SlotAnnotationValidator<CassetteUseSlotAnnotation> {

	@Inject CassetteUseSlotAnnotationDAO lCassetteUseDAO;
	@Inject CassetteDAO lCassetteDAO;
	@Inject FbcvTermDAO fbcvTermDAO;

	private static final String USE_NAMESPACE = "experimental_tool_descriptor";

	public ObjectResponse<CassetteUseSlotAnnotation> validateCassetteUseSlotAnnotation(CassetteUseSlotAnnotation uiEntity) {
		CassetteUseSlotAnnotation use = validateCassetteUseSlotAnnotation(uiEntity, false, false);
		response.setEntity(use);
		return response;
	}

	public CassetteUseSlotAnnotation validateCassetteUseSlotAnnotation(CassetteUseSlotAnnotation uiEntity, Boolean throwError, Boolean validateCassette) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CassetteUseSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		CassetteUseSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = lCassetteUseDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find CassetteUseSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new CassetteUseSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (CassetteUseSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateCassette) {
			Cassette singleCassette = validateRequiredEntity(lCassetteDAO, "singleCassette", uiEntity.getSingleCassette(), dbEntity.getSingleCassette());
			dbEntity.setSingleCassette(singleCassette);
		}

		List<FBCVTerm> uses = validateRequiredEntities(fbcvTermDAO, "uses", uiEntity.getUses(), dbEntity.getUses());
		// SCRUM-6746: a use is an FBcv term from the 'experimental_tool_descriptor' namespace
		if (uses != null) {
			for (FBCVTerm use : uses) {
				if (!USE_NAMESPACE.equals(use.getNamespace())) {
					addMessageResponse("uses", ValidationConstants.INVALID_MESSAGE + " (" + use.getCurie() + " is not in the " + USE_NAMESPACE + " namespace)");
				}
			}
		}
		dbEntity.setUses(uses);

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

}
