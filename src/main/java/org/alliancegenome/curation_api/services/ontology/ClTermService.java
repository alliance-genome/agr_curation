package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ClTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CLTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

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
