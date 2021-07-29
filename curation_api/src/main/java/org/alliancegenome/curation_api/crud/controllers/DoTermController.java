package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.DoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.rest.interfaces.DoTermRESTInterface;
import org.alliancegenome.curation_api.services.DoTermService;

@RequestScoped
public class DoTermController extends BaseController<DoTermService, DOTerm, DoTermDAO> implements DoTermRESTInterface {

	@Inject DoTermService doTermService;

	@Override
	@PostConstruct
	protected void init() {
		setService(doTermService);
	}

}
