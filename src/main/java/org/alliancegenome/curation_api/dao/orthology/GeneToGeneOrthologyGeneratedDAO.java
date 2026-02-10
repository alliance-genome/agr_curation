package org.alliancegenome.curation_api.dao.orthology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneToGeneOrthologyGeneratedDAO extends BaseSQLDAO<GeneToGeneOrthologyGenerated> {

	protected GeneToGeneOrthologyGeneratedDAO() {
		super(GeneToGeneOrthologyGenerated.class);
	}

	public List<Long> getAllOrthologyIds() {
		String sql = """
				SELECT g.id
				FROM genetogeneorthologygenerated g
				INNER JOIN genetogeneorthology o ON g.id = o.id
				WHERE o.obsolete = false AND o.internal = false
				ORDER BY g.id
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<GeneToGeneOrthologyGenerated> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}

		return entityManager.createQuery(
				"SELECT g FROM GeneToGeneOrthologyGenerated g WHERE g.id IN :ids",
				GeneToGeneOrthologyGenerated.class)
			.setParameter("ids", ids)
			.getResultList();
	}
}