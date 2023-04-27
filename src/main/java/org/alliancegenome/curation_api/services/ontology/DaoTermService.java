package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.DaoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class DaoTermService extends BaseOntologyTermService<DAOTerm, DaoTermDAO> {

	@Inject
	DaoTermDAO daoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(daoTermDAO);
	}

}
