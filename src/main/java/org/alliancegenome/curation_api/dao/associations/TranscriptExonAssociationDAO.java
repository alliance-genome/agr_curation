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
