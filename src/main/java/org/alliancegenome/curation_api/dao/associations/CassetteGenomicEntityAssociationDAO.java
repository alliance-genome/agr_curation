package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CassetteGenomicEntityAssociationDAO extends BaseSQLDAO<CassetteGenomicEntityAssociation> {

	protected CassetteGenomicEntityAssociationDAO() {
		super(CassetteGenomicEntityAssociation.class);
	}

}
