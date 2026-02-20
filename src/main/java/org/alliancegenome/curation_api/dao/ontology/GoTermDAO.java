package org.alliancegenome.curation_api.dao.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class GoTermDAO extends BaseSQLDAO<GOTerm> {

	protected GoTermDAO() {
		super(GOTerm.class);
	}

	public Map<String, Long> getAllGOIds() {
		String sql = """
						select id, curie
						from ontologyterm
						where ontologytermtype = :type
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("type", "GOTerm");
		List<Object[]> objects = query.getResultList();
		Map<String, Long> ensemblGeneMap = new HashMap<>();
		objects.forEach(object -> {
			ensemblGeneMap.put((String) object[1], (Long) object[0]);
		});
		return ensemblGeneMap;
	}

	public List<Long> getAllGOSearchResultIds() {
		String sql = """
				SELECT id
				FROM ontologyterm
				WHERE ontologytermtype = 'GOTerm'
				AND obsolete = false
				AND internal = false
				ORDER BY id
				""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<GOTerm> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String jpql = """
				SELECT DISTINCT g FROM GOTerm g
				LEFT JOIN FETCH g.synonyms
				WHERE g.id IN :ids
				""";
		return entityManager.createQuery(jpql, GOTerm.class)
			.setParameter("ids", ids)
			.getResultList();
	}
}
