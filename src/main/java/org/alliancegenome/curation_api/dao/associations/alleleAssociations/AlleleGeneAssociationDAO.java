package org.alliancegenome.curation_api.dao.associations.alleleAssociations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;

@ApplicationScoped
public class AlleleGeneAssociationDAO extends BaseSQLDAO<AlleleGeneAssociation> {

	protected AlleleGeneAssociationDAO() {
		super(AlleleGeneAssociation.class);
	}

}
