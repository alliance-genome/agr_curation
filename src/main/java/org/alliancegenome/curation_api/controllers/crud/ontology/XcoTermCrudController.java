package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XcoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;

@RequestScoped
public class XcoTermCrudController extends BaseOntologyTermController<XcoTermService, XCOTerm, XcoTermDAO> implements XcoTermCrudInterface {

	@Inject
	XcoTermService xcoTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(xcoTermService, XCOTerm.class);
	}

}
