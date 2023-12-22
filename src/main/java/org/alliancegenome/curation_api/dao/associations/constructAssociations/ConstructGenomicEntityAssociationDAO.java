package org.alliancegenome.curation_api.dao.associations.constructAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructGenomicEntityAssociationDAO extends BaseSQLDAO<ConstructGenomicEntityAssociation> {

	protected ConstructGenomicEntityAssociationDAO() {
		super(ConstructGenomicEntityAssociation.class);
	}

}
