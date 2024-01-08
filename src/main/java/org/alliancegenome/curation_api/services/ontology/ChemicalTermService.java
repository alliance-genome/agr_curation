package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ChemicalTermService extends BaseOntologyTermService<ChemicalTerm, ChemicalTermDAO> {

	@Inject
	ChemicalTermDAO chemicalTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(chemicalTermDAO);
	}

}
