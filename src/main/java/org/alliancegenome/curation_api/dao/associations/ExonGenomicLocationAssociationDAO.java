package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.ExonGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExonGenomicLocationAssociationDAO extends BaseSQLDAO<ExonGenomicLocationAssociation> {

	protected ExonGenomicLocationAssociationDAO() {
		super(ExonGenomicLocationAssociation.class);
	}

}
