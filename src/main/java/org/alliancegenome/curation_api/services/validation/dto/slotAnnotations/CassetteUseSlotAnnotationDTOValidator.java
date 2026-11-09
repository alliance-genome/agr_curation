package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.CassetteUseSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.FbcvTermService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: validates the use(s) of a Cassette.
 *
 * use_curies is required by LinkML, so an empty list is an error rather than simply leaving the
 * annotation with no uses - an annotation that names no use carries no information.
 */
@RequestScoped
public class CassetteUseSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<CassetteUseSlotAnnotation, CassetteUseSlotAnnotationDTO> {

	@Inject FbcvTermService fbcvTermService;

	public ObjectResponse<CassetteUseSlotAnnotation> validateCassetteUseSlotAnnotationDTO(CassetteUseSlotAnnotation annotation, CassetteUseSlotAnnotationDTO dto) {
		response = new ObjectResponse<CassetteUseSlotAnnotation>();

		if (annotation == null) {
			annotation = new CassetteUseSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);

		if (CollectionUtils.isEmpty(dto.getUseCuries())) {
			response.addErrorMessage("use_curies", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<FBCVTerm> uses = validateRequiredOntologyTerms(fbcvTermService, "use_curies", dto.getUseCuries());
			annotation.setUses(uses);
		}

		response.setEntity(annotation);
		return response;
	}
}
