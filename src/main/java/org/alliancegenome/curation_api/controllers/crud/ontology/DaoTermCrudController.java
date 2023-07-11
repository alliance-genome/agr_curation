package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.DaoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.DaoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class DaoTermCrudController extends BaseOntologyTermController<DaoTermService, DAOTerm, DaoTermDAO> implements DaoTermCrudInterface {

	@Inject
	DaoTermService daoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("FBbt");
		setService(daoTermService, DAOTerm.class, config);
	}

}
