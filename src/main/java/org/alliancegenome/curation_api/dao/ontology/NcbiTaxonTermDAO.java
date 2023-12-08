package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NcbiTaxonTermDAO extends BaseSQLDAO<NCBITaxonTerm> {
	
	protected NcbiTaxonTermDAO() {
		super(NCBITaxonTerm.class);
	}

}
