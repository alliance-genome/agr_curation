package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. */
@RequestScoped
public class CassetteSymbolSlotAnnotationValidator extends NameSlotAnnotationValidator<CassetteSymbolSlotAnnotation> {

	@Inject CassetteSymbolSlotAnnotationDAO lCassetteSymbolDAO;
	@Inject CassetteDAO lCassetteDAO;

	public ObjectResponse<CassetteSymbolSlotAnnotation> validateCassetteSymbolSlotAnnotation(CassetteSymbolSlotAnnotation uiEntity) {
		CassetteSymbolSlotAnnotation annotation = validateCassetteSymbolSlotAnnotation(uiEntity, false, false);
		response.setEntity(annotation);
		return response;
	}

	public CassetteSymbolSlotAnnotation validateCassetteSymbolSlotAnnotation(CassetteSymbolSlotAnnotation uiEntity, Boolean throwError, Boolean validateCassette) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CassetteSymbolSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		CassetteSymbolSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = lCassetteSymbolDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find CassetteSymbolSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new CassetteSymbolSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (CassetteSymbolSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

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
