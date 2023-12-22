package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.PatoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.PatoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.PATOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.PatoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PatoTermCrudController extends BaseOntologyTermController<PatoTermService, PATOTerm, PatoTermDAO> implements PatoTermCrudInterface {

	@Inject
	PatoTermService patoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		setService(patoTermService, PATOTerm.class, config);
	}

}
