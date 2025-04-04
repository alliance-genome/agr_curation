package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptGenomicLocationAssociationDAO extends BaseSQLDAO<TranscriptGenomicLocationAssociation> {

	protected TranscriptGenomicLocationAssociationDAO() {
		super(TranscriptGenomicLocationAssociation.class);
	}

}
