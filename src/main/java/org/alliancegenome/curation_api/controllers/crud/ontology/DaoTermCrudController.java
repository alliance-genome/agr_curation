package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.DaoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.DaoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;

@RequestScoped
public class DaoTermCrudController extends BaseOntologyTermController<DaoTermService, DAOTerm, DaoTermDAO> implements DaoTermCrudInterface {

	@Inject DaoTermService daoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("FBbt");
		setService(daoTermService, DAOTerm.class, config);
	}

}
