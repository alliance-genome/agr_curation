package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneGeneticInteractionDAO extends BaseCurieSQLDAO<GeneGeneticInteraction> {

	protected GeneGeneticInteractionDAO() {
		super(GeneGeneticInteraction.class);
	}

	public List<Long> getAllIds() {
		String sql = """
				SELECT id
				FROM genegeneticinteraction
				WHERE obsolete = false AND internal = false
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public Map<String, long[]> findInteractionIdMap() {
		List<Object[]> rows = entityManager
			.createNativeQuery("SELECT ggi.interactionid, ggi.id, ggi.uniqueid FROM genegeneticinteraction ggi WHERE ggi.interactionid IS NOT NULL")
			.getResultList();
		Map<String, long[]> map = new HashMap<>(rows.size());
		for (Object[] row : rows) {
			String interactionId = (String) row[0];
			Long id = ((Number) row[1]).longValue();
			long uniqueIdHash = row[2] != null ? ((String) row[2]).hashCode() : 0;
			map.put(interactionId, new long[]{id, uniqueIdHash});
		}
		return map;
	}

	public List<GeneGeneticInteraction> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}

		return entityManager.createQuery(
				"SELECT g FROM GeneGeneticInteraction g WHERE g.id IN :ids",
				GeneGeneticInteraction.class)
			.setParameter("ids", ids)
			.getResultList();
	}

}
