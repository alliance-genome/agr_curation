package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ClTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CLTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class ClTermService extends BaseOntologyTermService<CLTerm, ClTermDAO> {

	@Inject
	ClTermDAO clTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(clTermDAO);
	}

}
