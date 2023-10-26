package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleNomenclatureEventSlotAnnotationValidator extends SlotAnnotationValidator<AlleleNomenclatureEventSlotAnnotation> {

	@Inject
	AlleleNomenclatureEventSlotAnnotationDAO alleleNomenclatureEventDAO;
	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> validateAlleleNomenclatureEventSlotAnnotation(AlleleNomenclatureEventSlotAnnotation uiEntity) {
		AlleleNomenclatureEventSlotAnnotation nomenclatureEvent = validateAlleleNomenclatureEventSlotAnnotation(uiEntity, false, false);
		response.setEntity(nomenclatureEvent);
		return response;
	}

	public AlleleNomenclatureEventSlotAnnotation validateAlleleNomenclatureEventSlotAnnotation(AlleleNomenclatureEventSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleNomenclatureEventSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleNomenclatureEventSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleNomenclatureEventDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleNomenclatureEventSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleNomenclatureEventSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleNomenclatureEventSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		VocabularyTerm nomenclatureEvent = validateNomenclatureEvent(uiEntity, dbEntity);
		dbEntity.setNomenclatureEvent(nomenclatureEvent);
		
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

	private VocabularyTerm validateNomenclatureEvent(AlleleNomenclatureEventSlotAnnotation uiEntity, AlleleNomenclatureEventSlotAnnotation dbEntity) {
		String field = "nomenclatureEvent";

		if (uiEntity.getNomenclatureEvent() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm nomenclatureEvent = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_NOMENCLATURE_EVENT_VOCABULARY, uiEntity.getNomenclatureEvent().getName()).getEntity();
		if (nomenclatureEvent == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (nomenclatureEvent.getObsolete() && (dbEntity.getNomenclatureEvent() == null || !nomenclatureEvent.getName().equals(dbEntity.getNomenclatureEvent().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return nomenclatureEvent;
	}

}
