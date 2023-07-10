package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ClTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ClTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CLTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.ClTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ClTermCrudController extends BaseOntologyTermController<ClTermService, CLTerm, ClTermDAO> implements ClTermCrudInterface {

	@Inject
	ClTermService clTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("CL");
		setService(clTermService, CLTerm.class, config);
	}

}
