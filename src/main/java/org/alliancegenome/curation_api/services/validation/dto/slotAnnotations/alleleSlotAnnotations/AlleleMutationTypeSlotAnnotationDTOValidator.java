package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	SoTermService soTermService;

	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypeSlotAnnotationDTO(AlleleMutationTypeSlotAnnotation annotation, AlleleMutationTypeSlotAnnotationDTO dto) {
		ObjectResponse<AlleleMutationTypeSlotAnnotation> amsaResponse = new ObjectResponse<AlleleMutationTypeSlotAnnotation>();

		if (annotation == null)
			annotation = new AlleleMutationTypeSlotAnnotation();

		if (CollectionUtils.isEmpty(dto.getMutationTypeCuries())) {
			amsaResponse.addErrorMessage("mutation_type_curies", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<SOTerm> soTerms = new ArrayList<>();
			for (String soCurie : dto.getMutationTypeCuries()) {
				SOTerm soTerm = soTermService.findByCurieOrSecondaryId(soCurie);
				if (soTerm == null) {
					amsaResponse.addErrorMessage("mutation_type_curies", ValidationConstants.INVALID_MESSAGE + " (" + soCurie + ")");
					break;
				}
				soTerms.add(soTerm);
			}
			annotation.setMutationTypes(soTerms);
		}

		ObjectResponse<AlleleMutationTypeSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		amsaResponse.addErrorMessages(saResponse.getErrorMessages());

		amsaResponse.setEntity(annotation);

		return amsaResponse;
	}
}
