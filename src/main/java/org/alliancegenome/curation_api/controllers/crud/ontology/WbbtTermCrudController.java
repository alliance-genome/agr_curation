package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.WbbtTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBBTTerm;
import org.alliancegenome.curation_api.services.ontology.WbbtTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class WbbtTermCrudController extends BaseOntologyTermController<WbbtTermService, WBBTTerm, WbbtTermDAO> implements WbbtTermCrudInterface {

	@Inject
	WbbtTermService wbbtTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(wbbtTermService, WBBTTerm.class);
	}

}
