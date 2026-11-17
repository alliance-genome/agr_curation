package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Analysis;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.ingest.dto.AnalysisDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ontology.FbcvTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6714: direct-submission validator for Analysis, a cascade-owned sub-object (no independent
 * CRUD/curie minting) currently only reachable from HTPExpressionDatasetAnnotationDTO.
 */
@RequestScoped
public class AnalysisDTOValidator extends AuditedObjectDTOValidator<Analysis, AnalysisDTO> {

	@Inject FbcvTermService fbcvTermService;
	@Inject OrganizationService organizationService;
	@Inject CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;

	public ObjectResponse<Analysis> validateAnalysisDTO(AnalysisDTO dto, Analysis analysis) {
		response = new ObjectResponse<>();

		if (analysis == null) {
			analysis = new Analysis();
		}

		analysis = validateAuditedObjectDTO(analysis, dto);

		analysis.setAnalysisType(validateOntologyTerm(fbcvTermService, "analysis_type_curie", dto.getAnalysisTypeCurie()));

		if (StringUtils.isBlank(dto.getDataProvider())) {
			response.addErrorMessage("data_provider", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Organization> dataProviderResponse = organizationService.getByAbbr(dto.getDataProvider());
			if (dataProviderResponse == null || dataProviderResponse.getEntity() == null) {
				response.addErrorMessage("data_provider", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDataProvider() + ")");
			} else {
				analysis.setDataProvider(dataProviderResponse.getEntity());
			}
		}

		if (dto.getDataProviderCrossReferenceDto() != null) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getDataProviderCrossReferenceDto(), analysis.getDataProviderCrossReference());
			if (xrefResponse.hasErrors()) {
				response.addErrorMessage("data_provider_cross_reference_dto", xrefResponse.errorMessagesString());
			} else {
				// dataProviderCrossReference has no cascade at all, so a brand new CrossReference
				// needs persisting explicitly before it's assigned, even though Analysis itself is
				// cascade-persisted from its parent.
				analysis.setDataProviderCrossReference(crossReferenceDAO.persist(xrefResponse.getEntity()));
			}
		} else {
			analysis.setDataProviderCrossReference(null);
		}

		// Mutate the existing managed collection in place rather than replacing its reference -
		// relatedNotes has orphanRemoval=true, and Hibernate rejects a collection no longer
		// referenced by its owning entity at flush time if the reference itself is swapped out.
		if (analysis.getRelatedNotes() != null) {
			analysis.getRelatedNotes().clear();
		}
		List<Note> validatedNotes = validateNotes(dto.getRelatedNoteDtos(), null);
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			if (analysis.getRelatedNotes() == null) {
				analysis.setRelatedNotes(new ArrayList<>());
			}
			analysis.getRelatedNotes().addAll(validatedNotes);
		}

		response.setEntity(analysis);
		return response;
	}

}
