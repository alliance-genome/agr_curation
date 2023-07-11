package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ZfsTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ZfsTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZFSTerm;
import org.alliancegenome.curation_api.services.ontology.ZfsTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ZfsTermCrudController extends BaseOntologyTermController<ZfsTermService, ZFSTerm, ZfsTermDAO> implements ZfsTermCrudInterface {

	@Inject
	ZfsTermService zfsTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(zfsTermService, ZFSTerm.class);
	}

}
