package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.MpTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.MpTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.MpTermService;

@RequestScoped
public class MpTermController extends BaseController<MpTermService, MPTerm, MpTermDAO> implements MpTermRESTInterface {

	@Inject MpTermService mpTermService;

	@Override
	@PostConstruct
	protected void init() {
		setService(mpTermService);
	}

}
