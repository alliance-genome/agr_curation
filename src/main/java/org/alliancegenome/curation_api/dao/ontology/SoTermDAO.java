package org.alliancegenome.curation_api.dao.ontology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class SoTermDAO extends BaseSQLDAO<SOTerm> {

	protected SoTermDAO() {
		super(SOTerm.class);
	}

	public Map<String, Integer> getSeverityRanking() {
		Query query = entityManager.createNativeQuery(
			"SELECT name, severityorder FROM ontologyterm WHERE ontologytermtype = 'SOTerm' AND severityorder IS NOT NULL"
		);
		@SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();
		Map<String, Integer> ranking = new HashMap<>();
		for (Object[] row : rows) {
			ranking.put((String) row[0], (Integer) row[1]);
		}
		return ranking;
	}

}
