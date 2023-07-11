package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.EmapaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class EmapaTermService extends BaseOntologyTermService<EMAPATerm, EmapaTermDAO> {

	@Inject
	EmapaTermDAO emapaTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(emapaTermDAO);
	}

}
