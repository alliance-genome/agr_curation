package org.alliancegenome.curation_api.dao.associations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptExonAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptExonAssociationDAO extends BaseSQLDAO<TranscriptExonAssociation> {

	protected TranscriptExonAssociationDAO() {
		super(TranscriptExonAssociation.class);
	}

	public List<Long> findIdsByDataProvider(String dataProvider, String taxonCurie) {
		String jpql = "SELECT a.id FROM TranscriptExonAssociation a"
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

	public Map<Long, TranscriptExonAssociation> findByExonIds(Collection<Long> exonIds) {
		if (exonIds == null || exonIds.isEmpty()) {
			return new HashMap<>();
		}
		List<TranscriptExonAssociation> results = entityManager.createQuery(
				"SELECT a FROM TranscriptExonAssociation a"
				+ " WHERE a.transcriptExonAssociationObject.id IN :exonIds",
				TranscriptExonAssociation.class)
			.setParameter("exonIds", exonIds)
			.getResultList();
		Map<Long, TranscriptExonAssociation> map = new HashMap<>();
		for (TranscriptExonAssociation a : results) {
			map.put(a.getTranscriptExonAssociationObject().getId(), a);
		}
		return map;
	}

}
