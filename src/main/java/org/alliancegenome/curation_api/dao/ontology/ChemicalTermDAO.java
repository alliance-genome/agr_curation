package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChemicalTermDAO extends BaseSQLDAO<ChemicalTerm> {

	protected ChemicalTermDAO() {
		super(ChemicalTerm.class);
	}

}