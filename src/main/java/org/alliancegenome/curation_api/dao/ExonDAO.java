package org.alliancegenome.curation_api.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Exon;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExonDAO extends BaseSQLDAO<Exon> {

	protected ExonDAO() {
		super(Exon.class);
	}

	public List<Long> findIdsByDataProvider(String dataProvider, String taxonCurie) {
		String jpql = "SELECT e.id FROM Exon e WHERE e.dataProvider.abbreviation = :dp";
		if (taxonCurie != null) {
			jpql += " AND e.taxon.curie = :taxon";
		}
		var query = entityManager.createQuery(jpql, Long.class).setParameter("dp", dataProvider);
		if (taxonCurie != null) {
			query.setParameter("taxon", taxonCurie);
		}
		return query.getResultList();
	}

	public Map<String, Exon> findByUniqueIds(Collection<String> uniqueIds) {
		if (uniqueIds == null || uniqueIds.isEmpty()) {
			return new HashMap<>();
		}
		List<Exon> results = entityManager
			.createQuery("SELECT e FROM Exon e WHERE e.uniqueId IN :uniqueIds", Exon.class)
			.setParameter("uniqueIds", uniqueIds)
			.getResultList();
		Map<String, Exon> map = new HashMap<>();
		for (Exon exon : results) {
			map.put(exon.getUniqueId(), exon);
		}
		return map;
	}

}
