package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.WblsTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.WblsTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBLSTerm;
import org.alliancegenome.curation_api.services.ontology.WblsTermService;

@RequestScoped
public class WblsTermCrudController extends BaseOntologyTermController<WblsTermService, WBLSTerm, WblsTermDAO> implements WblsTermCrudInterface {

	@Inject
	WblsTermService wblsTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(wblsTermService, WBLSTerm.class);
	}

}
