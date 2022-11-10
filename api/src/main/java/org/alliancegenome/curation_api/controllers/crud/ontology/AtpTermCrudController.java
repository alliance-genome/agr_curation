package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.AtpTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.AtpTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.AtpTermService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class AtpTermCrudController extends BaseOntologyTermController<AtpTermService, ATPTerm, AtpTermDAO> implements AtpTermCrudInterface {

	@Inject
	AtpTermService atpTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("ATP");
		setService(atpTermService, ATPTerm.class, config);
	}

}
