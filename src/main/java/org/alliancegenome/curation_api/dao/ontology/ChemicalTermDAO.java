package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;

@ApplicationScoped
public class ChemicalTermDAO extends BaseSQLDAO<ChemicalTerm> {

	protected ChemicalTermDAO() {
		super(ChemicalTerm.class);
	}
	
}