package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleNomenclatureEventSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject VocabularyTermService vocabularyTermService;

	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> validateAlleleNomenclatureEventSlotAnnotationDTO(AlleleNomenclatureEventSlotAnnotation annotation, AlleleNomenclatureEventSlotAnnotationDTO dto) {
		ObjectResponse<AlleleNomenclatureEventSlotAnnotation> adsResponse = new ObjectResponse<AlleleNomenclatureEventSlotAnnotation>();

		if (annotation == null) {
			annotation = new AlleleNomenclatureEventSlotAnnotation();
		}

		VocabularyTerm nomenclatureEvent = null;
		if (StringUtils.isBlank(dto.getNomenclatureEventName())) {
			adsResponse.addErrorMessage("nomenclature_event_name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			nomenclatureEvent = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_NOMENCLATURE_EVENT_VOCABULARY, dto.getNomenclatureEventName()).getEntity();
			if (nomenclatureEvent == null) {
				adsResponse.addErrorMessage("nomenclature_event_name", ValidationConstants.INVALID_MESSAGE);
			}
		}
		annotation.setNomenclatureEvent(nomenclatureEvent);

		ObjectResponse<AlleleNomenclatureEventSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		adsResponse.addErrorMessages(saResponse.getErrorMessages());

		adsResponse.setEntity(annotation);

		return adsResponse;
	}
}
