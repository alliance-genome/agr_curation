package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.PwTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PWTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
