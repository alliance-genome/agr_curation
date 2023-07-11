package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.WbPhenotypeTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
