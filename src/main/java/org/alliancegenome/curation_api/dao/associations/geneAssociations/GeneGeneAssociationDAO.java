package org.alliancegenome.curation_api.dao.associations.geneAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.geneAssociations.GeneGeneAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneGeneAssociationDAO extends BaseSQLDAO<GeneGeneAssociation> {

	protected GeneGeneAssociationDAO() {
		super(GeneGeneAssociation.class);
	}

}
