package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.GeneGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneGenomicLocationAssociationDAO extends BaseSQLDAO<GeneGenomicLocationAssociation> {

	protected GeneGenomicLocationAssociationDAO() {
		super(GeneGenomicLocationAssociation.class);
	}

}
