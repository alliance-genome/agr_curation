package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.BioSampleGenomicInformation;
import org.alliancegenome.curation_api.model.ingest.dto.BioSampleGenomicInformationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6715: direct-submission validator for BioSampleGenomicInformation, a cascade-owned
 * sub-object (no independent CRUD) currently only reachable from
 * HTPExpressionDatasetSampleAnnotationDTO. Mirrors the FMS validator's allele-then-AGM fallback
 * order (HTPExpressionDatasetSampleAnnotationFmsDTOValidator.validateGenomicInformation).
 */
@RequestScoped
public class BioSampleGenomicInformationDTOValidator extends AuditedObjectDTOValidator<BioSampleGenomicInformation, BioSampleGenomicInformationDTO> {

	@Inject AlleleService alleleService;
	@Inject AffectedGenomicModelService affectedGenomicModelService;

	public ObjectResponse<BioSampleGenomicInformation> validateBioSampleGenomicInformationDTO(BioSampleGenomicInformationDTO dto, BioSampleGenomicInformation genomicInformation) {
		response = new ObjectResponse<>();

		if (genomicInformation == null) {
			genomicInformation = new BioSampleGenomicInformation();
		}

		genomicInformation = validateAuditedObjectDTO(genomicInformation, dto);

		if (StringUtils.isBlank(dto.getBioSampleText()) && StringUtils.isBlank(dto.getBioSampleAlleleCurie()) && StringUtils.isBlank(dto.getBioSampleAgmCurie())) {
			response.addErrorMessage("biosample_text", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " biosample_allele_curie or biosample_agm_curie");
		}

		genomicInformation.setBioSampleText(handleStringField(dto.getBioSampleText()));

		if (StringUtils.isNotBlank(dto.getBioSampleAlleleCurie())) {
			Allele allele = alleleService.findByIdentifierString(dto.getBioSampleAlleleCurie());
			if (allele == null) {
				response.addErrorMessage("biosample_allele_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getBioSampleAlleleCurie() + ")");
			} else {
				genomicInformation.setBioSampleAllele(allele);
			}
		} else {
			genomicInformation.setBioSampleAllele(null);
		}

		if (StringUtils.isNotBlank(dto.getBioSampleAgmCurie())) {
			AffectedGenomicModel agm = affectedGenomicModelService.findByIdentifierString(dto.getBioSampleAgmCurie());
			if (agm == null) {
				response.addErrorMessage("biosample_agm_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getBioSampleAgmCurie() + ")");
			} else {
				genomicInformation.setBioSampleAgm(agm);
				genomicInformation.setBioSampleAgmType(validateTermInVocabulary("biosample_agm_type", dto.getBioSampleAgmType(), VocabularyConstants.AGM_SUBTYPE_VOCABULARY));
			}
		} else {
			genomicInformation.setBioSampleAgm(null);
			genomicInformation.setBioSampleAgmType(null);
		}

		response.setEntity(genomicInformation);
		return response;
	}

}
