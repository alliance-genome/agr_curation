package org.alliancegenome.curation_api.dao.associations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptGenomicLocationAssociationDAO extends BaseSQLDAO<TranscriptGenomicLocationAssociation> {

	protected TranscriptGenomicLocationAssociationDAO() {
		super(TranscriptGenomicLocationAssociation.class);
	}

	public List<Long> findIdsByDataProvider(String dataProvider, String taxonCurie) {
		String jpql = "SELECT a.id FROM TranscriptGenomicLocationAssociation a"
			+ " WHERE a.transcriptAssociationSubject.dataProvider.abbreviation = :dp";
		if (taxonCurie != null) {
			jpql += " AND a.transcriptAssociationSubject.taxon.curie = :taxon";
		}
		var query = entityManager.createQuery(jpql, Long.class).setParameter("dp", dataProvider);
		if (taxonCurie != null) {
			query.setParameter("taxon", taxonCurie);
		}
		return query.getResultList();
	}

	public Map<Long, TranscriptGenomicLocationAssociation> findByTranscriptIdsAndAssembly(Collection<Long> transcriptIds, String assemblyId) {
		if (transcriptIds == null || transcriptIds.isEmpty()) {
			return new HashMap<>();
		}
		List<TranscriptGenomicLocationAssociation> results = entityManager.createQuery(
				"SELECT a FROM TranscriptGenomicLocationAssociation a"
				+ " WHERE a.transcriptAssociationSubject.id IN :transcriptIds"
				+ " AND a.transcriptGenomicLocationAssociationObject.genomeAssembly.primaryExternalId = :assemblyId",
				TranscriptGenomicLocationAssociation.class)
			.setParameter("transcriptIds", transcriptIds)
			.setParameter("assemblyId", assemblyId)
			.getResultList();
		Map<Long, TranscriptGenomicLocationAssociation> map = new HashMap<>();
		for (TranscriptGenomicLocationAssociation a : results) {
			map.put(a.getTranscriptAssociationSubject().getId(), a);
		}
		return map;
	}

}
