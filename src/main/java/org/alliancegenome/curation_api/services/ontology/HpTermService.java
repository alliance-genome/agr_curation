package org.alliancegenome.curation_api.services.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.HpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.HPTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

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
