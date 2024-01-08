package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.WbPhenotypeTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class WbPhenotypeTermService extends BaseOntologyTermService<WBPhenotypeTerm, WbPhenotypeTermDAO> {

	@Inject
	WbPhenotypeTermDAO wbPhenotypeTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(wbPhenotypeTermDAO);
	}

}
