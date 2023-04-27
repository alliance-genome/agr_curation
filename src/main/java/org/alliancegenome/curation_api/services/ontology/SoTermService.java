package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class SoTermService extends BaseOntologyTermService<SOTerm, SoTermDAO> {

	@Inject
	SoTermDAO soTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(soTermDAO);
	}

}
