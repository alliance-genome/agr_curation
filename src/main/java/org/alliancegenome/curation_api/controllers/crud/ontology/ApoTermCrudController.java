package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ApoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ApoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.APOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.ApoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ApoTermCrudController extends BaseOntologyTermController<ApoTermService, APOTerm, ApoTermDAO> implements ApoTermCrudInterface {

	@Inject
	ApoTermService apoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("experiment_type");
		config.getAltNameSpaces().add("mutant_type");
		config.getAltNameSpaces().add("observable");
		config.getAltNameSpaces().add("qualifier");
		setService(apoTermService, APOTerm.class, config);
	}

}
