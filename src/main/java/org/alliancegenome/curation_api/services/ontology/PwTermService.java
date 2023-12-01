package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.PwTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PWTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PwTermService extends BaseOntologyTermService<PWTerm, PwTermDAO> {

	@Inject
	PwTermDAO pwTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(pwTermDAO);
	}

}
