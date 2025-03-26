package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptCodingSequenceAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptCodingSequenceAssociationDAO extends BaseSQLDAO<TranscriptCodingSequenceAssociation> {

	protected TranscriptCodingSequenceAssociationDAO() {
		super(TranscriptCodingSequenceAssociation.class);
	}

}
