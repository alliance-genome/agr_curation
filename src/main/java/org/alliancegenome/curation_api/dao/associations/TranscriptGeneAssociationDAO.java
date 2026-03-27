package org.alliancegenome.curation_api.dao.associations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGeneAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptGeneAssociationDAO extends BaseSQLDAO<TranscriptGeneAssociation> {

	protected TranscriptGeneAssociationDAO() {
		super(TranscriptGeneAssociation.class);
	}

	public List<Long> findIdsByDataProvider(String dataProvider, String taxonCurie) {
		String jpql = "SELECT a.id FROM TranscriptGeneAssociation a"
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

	public Map<Long, TranscriptGeneAssociation> findByTranscriptIds(Collection<Long> transcriptIds) {
		if (transcriptIds == null || transcriptIds.isEmpty()) {
			return new HashMap<>();
		}
		List<TranscriptGeneAssociation> results = entityManager.createQuery(
				"SELECT a FROM TranscriptGeneAssociation a"
				+ " WHERE a.transcriptAssociationSubject.id IN :transcriptIds",
				TranscriptGeneAssociation.class)
			.setParameter("transcriptIds", transcriptIds)
			.getResultList();
		Map<Long, TranscriptGeneAssociation> map = new HashMap<>();
		for (TranscriptGeneAssociation a : results) {
			map.put(a.getTranscriptAssociationSubject().getId(), a);
		}
		return map;
	}

}
