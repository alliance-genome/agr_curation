package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

@RequestScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationValidator extends SlotAnnotationValidator<AlleleGermlineTransmissionStatusSlotAnnotation> {

	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationDAO alleleGermlineTransmissionStatusDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> validateAlleleGermlineTransmissionStatusSlotAnnotation(AlleleGermlineTransmissionStatusSlotAnnotation uiEntity) {
		AlleleGermlineTransmissionStatusSlotAnnotation germlineTransmissionStatus = validateAlleleGermlineTransmissionStatusSlotAnnotation(uiEntity, false, false);
		response.setEntity(germlineTransmissionStatus);
		return response;
	}

	public AlleleGermlineTransmissionStatusSlotAnnotation validateAlleleGermlineTransmissionStatusSlotAnnotation(AlleleGermlineTransmissionStatusSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleGermlineTransmissionStatusSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleGermlineTransmissionStatusSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleGermlineTransmissionStatusDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleGermlineTransmissionStatusSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleGermlineTransmissionStatusSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (AlleleGermlineTransmissionStatusSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm gts = validateGermlineTransmissionStatus(uiEntity, dbEntity);
		dbEntity.setGermlineTransmissionStatus(gts);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
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
	
	private VocabularyTerm validateGermlineTransmissionStatus(AlleleGermlineTransmissionStatusSlotAnnotation uiEntity, AlleleGermlineTransmissionStatusSlotAnnotation dbEntity) {
		String field = "germlineTransmissionStatus";
		if (uiEntity.getGermlineTransmissionStatus() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm gts = vocabularyTermService.getTermInVocabulary(VocabularyConstants.GERMLINE_TRANSMISSION_STATUS_VOCABULARY, uiEntity.getGermlineTransmissionStatus().getName()).getEntity();
		if (gts == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (gts.getObsolete() && (dbEntity.getGermlineTransmissionStatus() == null || !gts.getName().equals(dbEntity.getGermlineTransmissionStatus().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return gts;
	}

}
