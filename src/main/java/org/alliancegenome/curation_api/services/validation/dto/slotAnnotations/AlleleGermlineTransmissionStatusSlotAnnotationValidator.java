package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationValidator extends SlotAnnotationValidator<AlleleGermlineTransmissionStatusSlotAnnotation> {

	@Inject AlleleGermlineTransmissionStatusSlotAnnotationDAO alleleGermlineTransmissionStatusDAO;
	@Inject AlleleDAO alleleDAO;

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

		VocabularyTerm gts = validateRequiredTermInVocabulary("germlineTransmissionStatus", VocabularyConstants.GERMLINE_TRANSMISSION_STATUS_VOCABULARY, uiEntity.getGermlineTransmissionStatus(), dbEntity.getGermlineTransmissionStatus());
		dbEntity.setGermlineTransmissionStatus(gts);

		if (validateAllele) {
			Allele singleAllele = validateRequiredEntity(alleleDAO, "singleAllele", uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
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

}
