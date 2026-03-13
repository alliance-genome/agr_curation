package org.alliancegenome.curation_api.dao.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class DoTermDAO extends BaseSQLDAO<DOTerm> {

	protected DoTermDAO() {
		super(DOTerm.class);
	}

	public List<String> getDoTermCuries() {
		String sql = """
			SELECT curie
			FROM ontologyterm
			WHERE ontologytermtype = 'DOTerm'
		""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();

		objects.forEach(object -> {
			list.add((String) object);
		});

		return list;
	}

	public List<Long> getAllIds() {
		String sql = """
			SELECT id
			FROM ontologyterm
			WHERE ontologytermtype = 'DOTerm'
			AND obsolete = false
			AND internal = false
			ORDER BY id
			""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<DOTerm> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String jpql = """
			SELECT DISTINCT d FROM DOTerm d
			LEFT JOIN FETCH d.synonyms
			WHERE d.id IN :ids
			""";
		return entityManager.createQuery(jpql, DOTerm.class)
			.setParameter("ids", ids)
			.getResultList();
	}

}
