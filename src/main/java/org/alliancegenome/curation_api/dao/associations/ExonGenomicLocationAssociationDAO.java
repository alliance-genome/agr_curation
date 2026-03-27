package org.alliancegenome.curation_api.dao.associations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.ExonGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExonGenomicLocationAssociationDAO extends BaseSQLDAO<ExonGenomicLocationAssociation> {

	protected ExonGenomicLocationAssociationDAO() {
		super(ExonGenomicLocationAssociation.class);
	}

	public Map<Long, ExonGenomicLocationAssociation> findByExonIdsAndAssembly(Collection<Long> exonIds, String assemblyId) {
		if (exonIds == null || exonIds.isEmpty()) {
			return new HashMap<>();
		}
		List<ExonGenomicLocationAssociation> results = entityManager.createQuery(
				"SELECT a FROM ExonGenomicLocationAssociation a" +
				" WHERE a.exonAssociationSubject.id IN :exonIds" +
				" AND a.exonGenomicLocationAssociationObject.genomeAssembly.primaryExternalId = :assemblyId",
				ExonGenomicLocationAssociation.class)
			.setParameter("exonIds", exonIds)
			.setParameter("assemblyId", assemblyId)
			.getResultList();
		Map<Long, ExonGenomicLocationAssociation> map = new HashMap<>();
		for (ExonGenomicLocationAssociation a : results) {
			map.put(a.getExonAssociationSubject().getId(), a);
		}
		return map;
	}

}
