package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.RoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.RoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ROTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.RoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class RoTermCrudController extends BaseOntologyTermController<RoTermService, ROTerm, RoTermDAO> implements RoTermCrudInterface {

	@Inject
	RoTermService roTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadObjectProperties(true);
		config.setLoadOnlyIRIPrefix("RO");
		setService(roTermService, ROTerm.class);
	}

}
