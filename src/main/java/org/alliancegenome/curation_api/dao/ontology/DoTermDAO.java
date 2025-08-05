package org.alliancegenome.curation_api.dao.ontology;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

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

}
