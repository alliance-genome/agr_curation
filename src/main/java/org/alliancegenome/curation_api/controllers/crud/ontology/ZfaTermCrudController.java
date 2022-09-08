package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ZfaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ZfaTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.services.ontology.ZfaTermService;

@RequestScoped
public class ZfaTermCrudController extends BaseOntologyTermController<ZfaTermService, ZFATerm, ZfaTermDAO> implements ZfaTermCrudInterface {

	@Inject
	ZfaTermService zfaTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(zfaTermService, ZFATerm.class);
	}

}
