package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGeneAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranscriptGeneAssociationDAO extends BaseSQLDAO<TranscriptGeneAssociation> {

	protected TranscriptGeneAssociationDAO() {
		super(TranscriptGeneAssociation.class);
	}

}
