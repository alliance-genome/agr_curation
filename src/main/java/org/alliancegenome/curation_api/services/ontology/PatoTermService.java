package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.PatoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PATOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PatoTermService extends BaseOntologyTermService<PATOTerm, PatoTermDAO> {

	@Inject
	PatoTermDAO patoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(patoTermDAO);
	}

}
