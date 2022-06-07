package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.EmapaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.EmapaTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.EmapaTermService;

@RequestScoped
public class EmapaTermCrudController extends BaseOntologyTermController<EmapaTermService, EMAPATerm, EmapaTermDAO> implements EmapaTermCrudInterface {

	@Inject EmapaTermService emapaTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("anatomical_structure");
		setService(emapaTermService, EMAPATerm.class, config);
	}

}
