package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptExonAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptExonAssociationDAO extends BaseSQLDAO<TranscriptExonAssociation> {

	protected TranscriptExonAssociationDAO() {
		super(TranscriptExonAssociation.class);
	}

}
