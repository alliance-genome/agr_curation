package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.EmapaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.EmapaTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.EmapaTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class EmapaTermCrudController extends BaseOntologyTermController<EmapaTermService, EMAPATerm, EmapaTermDAO> implements EmapaTermCrudInterface {

	@Inject
	EmapaTermService emapaTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("anatomical_structure");
		setService(emapaTermService, EMAPATerm.class, config);
	}

}
