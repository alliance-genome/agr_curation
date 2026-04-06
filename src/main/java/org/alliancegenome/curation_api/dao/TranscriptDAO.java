package org.alliancegenome.curation_api.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public List<Long> findIdsByDataProvider(String dataProvider, String taxonCurie) {
		String jpql = "SELECT t.id FROM Transcript t WHERE t.dataProvider.abbreviation = :dp";
		if (taxonCurie != null) {
			jpql += " AND t.taxon.curie = :taxon";
		}
		var query = entityManager.createQuery(jpql, Long.class).setParameter("dp", dataProvider);
		if (taxonCurie != null) {
			query.setParameter("taxon", taxonCurie);
		}
		return query.getResultList();
	}

	public Map<String, Transcript> findByModInternalIds(Collection<String> modInternalIds) {
		if (modInternalIds == null || modInternalIds.isEmpty()) {
			return new HashMap<>();
		}
		List<Transcript> results = entityManager
			.createQuery("SELECT t FROM Transcript t WHERE t.modInternalId IN :ids", Transcript.class)
			.setParameter("ids", modInternalIds)
			.getResultList();
		Map<String, Transcript> map = new HashMap<>();
		for (Transcript t : results) {
			map.put(t.getModInternalId(), t);
		}
		return map;
	}

	public Boolean hasReferencingPredictedVariantConsequences(Long transcriptId) {
		SearchResponse<PredictedVariantConsequence> response = pvcDAO.findByField(EntityFieldConstants.VARIANT_TRANSCRIPT + ".id", transcriptId);
		if (response != null && response.getSingleResult() != null) {
			return true;
		}
		return false;
	}

}
