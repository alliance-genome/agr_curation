package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. */
@RequestScoped
public class CassetteSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<CassetteSynonymSlotAnnotation> {

	@Inject CassetteSynonymSlotAnnotationDAO lCassetteSynonymDAO;
	@Inject CassetteDAO lCassetteDAO;

	public ObjectResponse<CassetteSynonymSlotAnnotation> validateCassetteSynonymSlotAnnotation(CassetteSynonymSlotAnnotation uiEntity) {
		CassetteSynonymSlotAnnotation annotation = validateCassetteSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(annotation);
		return response;
	}

	public CassetteSynonymSlotAnnotation validateCassetteSynonymSlotAnnotation(CassetteSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateCassette) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CassetteSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		CassetteSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = lCassetteSynonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find CassetteSynonymSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new CassetteSynonymSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (CassetteSynonymSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);


		if (validateCassette) {
			Cassette singleCassette = validateRequiredEntity(lCassetteDAO, "singleCassette", uiEntity.getSingleCassette(), dbEntity.getSingleCassette());
			dbEntity.setSingleCassette(singleCassette);
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
