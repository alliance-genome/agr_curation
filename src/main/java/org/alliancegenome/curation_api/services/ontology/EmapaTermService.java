package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.EmapaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
