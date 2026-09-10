package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.GenomicEntityDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VariantDTOValidator extends GenomicEntityDTOValidator<Variant, VariantDTO> {

	@Inject VariantDAO variantDAO;
	@Inject CurieMintService curieMintService;
	@Inject SoTermService soTermService;

	@Transactional
	public ObjectResponse<Variant> validateVariantDTO(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<Variant>();
		
		Variant variant = null;
		if (StringUtils.isNotBlank(dto.getPrimaryExternalId())) {
			variant = findDatabaseObject(variantDAO, "primaryExternalId", dto.getPrimaryExternalId());
		}
		if (variant == null) {
			if (StringUtils.isBlank(dto.getModInternalId())) {
				if (StringUtils.isBlank(dto.getPrimaryExternalId())) {
					response.addErrorMessage("mod_internal_id", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " primary_external_id");
				}
			} else {
				variant = findDatabaseObject(variantDAO, "modInternalId", dto.getModInternalId());
			}
		}
		if (variant == null) {
			variant = new Variant();
		}

		variant = validateGenomicEntityDTO(variant, dto, dataProvider, VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET);
		
		SOTerm variantType = validateRequiredOntologyTerm(soTermService, "variant_type_curie", dto.getVariantTypeCurie());
		variant.setVariantType(variantType);

		VocabularyTerm variantStatus = validateTermInVocabulary("variant_status_name", dto.getVariantStatusName(), VocabularyConstants.VARIANT_STATUS_VOCABULARY);
		variant.setVariantStatus(variantStatus);

		SOTerm sourceGeneralConsequence = validateOntologyTerm(soTermService, "source_general_consequence_curie", dto.getSourceGeneralConsequenceCurie());
		variant.setSourceGeneralConsequence(sourceGeneralConsequence);
		
		response.convertWarningMessagesToMap();
		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		// SCRUM-6077: mint an AGRKB curie for a new variant that has none, in the same transaction as
		// the insert below. No is-new guard is needed here, unlike VariantValidator: nothing in the DTO
		// field-copy chain assigns curie, so a re-load resolves to the stored variant whose curie is
		// already set and this is a no-op.
		//
		// The re-load matches above on primary_external_id, falling back to mod_internal_id, and a DTO
		// carrying neither is rejected before this point. Every variant in production is identified by
		// mod_internal_id — 100,529 of 100,529, unique and backed by biologicalentity_modinternalid_uk —
		// so that fallback is the branch that actually runs and a variant's AGRKB id survives a reload.
		// Note the cross-reference (allele id) is NOT usable for this: many alleles map to several
		// variants, as recorded on the ticket.
		curieMintService.mintCurieIfAbsent(variant);
		response.setEntity(variantDAO.persist(variant));
		
		return response;
	}

}
