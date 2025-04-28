package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.AgmSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<AgmSynonymSlotAnnotation> {

	@Inject AgmSynonymSlotAnnotationDAO agmSynonymDAO;
	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	public ObjectResponse<AgmSynonymSlotAnnotation> validateAgmSynonymSlotAnnotation(AgmSynonymSlotAnnotation uiEntity) {
		AgmSynonymSlotAnnotation synonym = validateAgmSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(synonym);
		return response;
	}

	public AgmSynonymSlotAnnotation validateAgmSynonymSlotAnnotation(AgmSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateAgm) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AgmSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AgmSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = agmSynonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AgmSynonymSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AgmSynonymSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (AgmSynonymSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabulary("nameType", VocabularyConstants.NAME_TYPE_VOCABULARY, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateAgm) {
			AffectedGenomicModel singleAgm = validateRequiredEntity(affectedGenomicModelDAO, "singleAgm", uiEntity.getSingleAgm(), dbEntity.getSingleAgm());
			dbEntity.setSingleAgm(singleAgm);
		}

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
