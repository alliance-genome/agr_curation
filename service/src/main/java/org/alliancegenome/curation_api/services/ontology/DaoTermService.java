package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.DaoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class DaoTermService extends BaseOntologyTermService<DAOTerm, DaoTermDAO> {

	@Inject DaoTermDAO daoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(daoTermDAO);
	}
	
}
