package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.BtoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.BTOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BtoTermService extends BaseOntologyTermService<BTOTerm, BtoTermDAO> {

	@Inject
	BtoTermDAO btoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(btoTermDAO);
	}

}
