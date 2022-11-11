package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class ChemicalTermService extends BaseOntologyTermService<ChemicalTerm, ChemicalTermDAO> {

	@Inject ChemicalTermDAO chemicalTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(chemicalTermDAO);
	}
	
}
