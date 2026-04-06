package org.alliancegenome.curation_api.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CodingSequence;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CodingSequenceDAO extends BaseSQLDAO<CodingSequence> {

	protected CodingSequenceDAO() {
		super(CodingSequence.class);
	}

	public List<Long> findIdsByDataProvider(String dataProvider, String taxonCurie) {
		String jpql = "SELECT c.id FROM CodingSequence c WHERE c.dataProvider.abbreviation = :dp";
		if (taxonCurie != null) {
			jpql += " AND c.taxon.curie = :taxon";
		}
		var query = entityManager.createQuery(jpql, Long.class).setParameter("dp", dataProvider);
		if (taxonCurie != null) {
			query.setParameter("taxon", taxonCurie);
		}
		return query.getResultList();
	}

	public Map<String, CodingSequence> findByUniqueIds(Collection<String> uniqueIds) {
		if (uniqueIds == null || uniqueIds.isEmpty()) {
			return new HashMap<>();
		}
		List<CodingSequence> results = entityManager
			.createQuery("SELECT c FROM CodingSequence c WHERE c.uniqueId IN :uniqueIds", CodingSequence.class)
			.setParameter("uniqueIds", uniqueIds)
			.getResultList();
		Map<String, CodingSequence> map = new HashMap<>();
		for (CodingSequence cds : results) {
			map.put(cds.getUniqueId(), cds);
		}
		return map;
	}

}
