package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XcoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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
