package org.alliancegenome.curation_api.dao.associations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CodingSequenceGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CodingSequenceGenomicLocationAssociationDAO extends BaseSQLDAO<CodingSequenceGenomicLocationAssociation> {

	protected CodingSequenceGenomicLocationAssociationDAO() {
		super(CodingSequenceGenomicLocationAssociation.class);
	}

	public Map<Long, CodingSequenceGenomicLocationAssociation> findByCdsIdsAndAssembly(Collection<Long> cdsIds, String assemblyId) {
		if (cdsIds == null || cdsIds.isEmpty()) {
			return new HashMap<>();
		}
		List<CodingSequenceGenomicLocationAssociation> results = entityManager.createQuery(
				"SELECT a FROM CodingSequenceGenomicLocationAssociation a"
				+ " WHERE a.codingSequenceAssociationSubject.id IN :cdsIds"
				+ " AND a.codingSequenceGenomicLocationAssociationObject.genomeAssembly.primaryExternalId = :assemblyId",
				CodingSequenceGenomicLocationAssociation.class)
			.setParameter("cdsIds", cdsIds)
			.setParameter("assemblyId", assemblyId)
			.getResultList();
		Map<Long, CodingSequenceGenomicLocationAssociation> map = new HashMap<>();
		for (CodingSequenceGenomicLocationAssociation a : results) {
			map.put(a.getCodingSequenceAssociationSubject().getId(), a);
		}
		return map;
	}

}
