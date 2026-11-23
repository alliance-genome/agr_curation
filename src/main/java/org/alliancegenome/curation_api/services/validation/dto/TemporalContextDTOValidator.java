package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.TemporalContext;
import org.alliancegenome.curation_api.model.ingest.dto.TemporalContextDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.StageTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6715: direct-submission validator for TemporalContext, a cascade-owned sub-object (no
 * independent CRUD) currently only reachable from BioSampleAgeDTO. Nothing to reuse - no non-FMS
 * validator exists for this entity anywhere else in the codebase.
 */
@RequestScoped
public class TemporalContextDTOValidator extends AuditedObjectDTOValidator<TemporalContext, TemporalContextDTO> {

	@Inject StageTermService stageTermService;

	public ObjectResponse<TemporalContext> validateTemporalContextDTO(TemporalContextDTO dto, TemporalContext temporalContext) {
		response = new ObjectResponse<>();

		if (temporalContext == null) {
			temporalContext = new TemporalContext();
		}

		temporalContext = validateAuditedObjectDTO(temporalContext, dto);

		temporalContext.setDevelopmentalStageStart(validateOntologyTerm(stageTermService, "developmental_stage_start_curie", dto.getDevelopmentalStageStartCurie()));
		temporalContext.setDevelopmentalStageStop(validateOntologyTerm(stageTermService, "developmental_stage_stop_curie", dto.getDevelopmentalStageStopCurie()));
		temporalContext.setAge(handleStringField(dto.getAge()));
		temporalContext.setStageUberonSlimTerms(validateTermsInVocabulary("stage_uberon_slim_terms", dto.getStageUberonSlimTerms(), VocabularyConstants.STAGE_UBERON_SLIM_TERMS));

		response.setEntity(temporalContext);
		return response;
	}

}
