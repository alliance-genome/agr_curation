package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.HpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.HPTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class HpTermService extends BaseOntologyTermService<HPTerm, HpTermDAO> {

	@Inject
	HpTermDAO hpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(hpTermDAO);
	}

}
