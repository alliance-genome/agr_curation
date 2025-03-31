package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.BtoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.BtoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.BTOTerm;
import org.alliancegenome.curation_api.services.ontology.BtoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BtoTermCrudController extends BaseOntologyTermController<BtoTermService, BTOTerm, BtoTermDAO> implements BtoTermCrudInterface {

	@Inject
	BtoTermService btoTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(btoTermService, BTOTerm.class);
	}

}
