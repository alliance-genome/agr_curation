package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

@RequestScoped
public class AlleleDatabaseStatusSlotAnnotationValidator extends SlotAnnotationValidator<AlleleDatabaseStatusSlotAnnotation> {

	@Inject
	AlleleDatabaseStatusSlotAnnotationDAO alleleDatabaseStatusDAO;
	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> validateAlleleDatabaseStatusSlotAnnotation(AlleleDatabaseStatusSlotAnnotation uiEntity) {
		AlleleDatabaseStatusSlotAnnotation mutationType = validateAlleleDatabaseStatusSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleDatabaseStatusSlotAnnotation validateAlleleDatabaseStatusSlotAnnotation(AlleleDatabaseStatusSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleDatabaseStatusSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleDatabaseStatusSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleDatabaseStatusDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleDatabaseStatusSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleDatabaseStatusSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleDatabaseStatusSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		VocabularyTerm databaseStatus = validateDatabaseStatus(uiEntity, dbEntity);
		dbEntity.setDatabaseStatus(databaseStatus);
		
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

	private VocabularyTerm validateDatabaseStatus(AlleleDatabaseStatusSlotAnnotation uiEntity, AlleleDatabaseStatusSlotAnnotation dbEntity) {
		String field = "databaseStatus";

		if (uiEntity.getDatabaseStatus() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm databaseStatus = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_DATABASE_STATUS_VOCABULARY, uiEntity.getDatabaseStatus().getName()).getEntity();
		if (databaseStatus == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (databaseStatus.getObsolete() && (dbEntity.getDatabaseStatus() == null || !databaseStatus.getName().equals(dbEntity.getDatabaseStatus().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return databaseStatus;
	}

}
