package org.alliancegenome.curation_api.dao.associations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptCodingSequenceAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptCodingSequenceAssociationDAO extends BaseSQLDAO<TranscriptCodingSequenceAssociation> {

	protected TranscriptCodingSequenceAssociationDAO() {
		super(TranscriptCodingSequenceAssociation.class);
	}

	public Map<Long, TranscriptCodingSequenceAssociation> findByCdsIds(Collection<Long> cdsIds) {
		if (cdsIds == null || cdsIds.isEmpty()) {
			return new HashMap<>();
		}
		List<TranscriptCodingSequenceAssociation> results = entityManager.createQuery(
				"SELECT a FROM TranscriptCodingSequenceAssociation a"
				+ " WHERE a.transcriptCodingSequenceAssociationObject.id IN :cdsIds",
				TranscriptCodingSequenceAssociation.class)
			.setParameter("cdsIds", cdsIds)
			.getResultList();
		Map<Long, TranscriptCodingSequenceAssociation> map = new HashMap<>();
		for (TranscriptCodingSequenceAssociation a : results) {
			map.put(a.getTranscriptCodingSequenceAssociationObject().getId(), a);
		}
		return map;
	}

}
