package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class TranscriptDAO extends BaseSQLDAO<Transcript> {
	
	@Inject PredictedVariantConsequenceDAO pvcDAO;

	protected TranscriptDAO() {
		super(Transcript.class);
	}

	public Boolean hasReferencingPredictedVariantConsequences(Long transcriptId) {
		SearchResponse<PredictedVariantConsequence> response = pvcDAO.findByField(EntityFieldConstants.VARIANT_TRANSCRIPT + ".id", transcriptId);
		if (response != null && response.getSingleResult() != null) {
			return true;
		}
		return false;
	}

}
