package org.alliancegenome.curation_api.dao.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleVariantAssociation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class AlleleVariantAssociationDAO extends BaseSQLDAO<AlleleVariantAssociation> {

	protected AlleleVariantAssociationDAO() {
		super(AlleleVariantAssociation.class);
	}

	public Map<String, Long> getAlleleVariantAssociationMap() {
		String hql = """
			select ava.alleleAssociationSubject.primaryExternalId, count(ava.id)
			from AlleleVariantAssociation ava
			where ava.alleleAssociationSubject.obsolete = false
			and ava.alleleAssociationSubject.internal = false
			group by ava.alleleAssociationSubject.primaryExternalId
			""";
		Query query = entityManager.createQuery(hql);
		List<Object[]> results = query.getResultList();

		Map<String, Long> resultMap = new HashMap<>();
		for (Object[] row : results) {
			String primaryExternalId = (String) row[0];
			Long count = (Long) row[1];
			resultMap.put(primaryExternalId, count);
		}

		return resultMap;
	}

}
