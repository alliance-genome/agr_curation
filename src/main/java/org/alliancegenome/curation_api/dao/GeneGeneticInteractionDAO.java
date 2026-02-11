package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneGeneticInteractionDAO extends BaseSQLDAO<GeneGeneticInteraction> {

	protected GeneGeneticInteractionDAO() {
		super(GeneGeneticInteraction.class);
	}

	public List<Long> getAllIds() {
		String sql = """
				SELECT id
				FROM genegeneticinteraction
				WHERE obsolete = false AND internal = false
				ORDER BY id
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
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
