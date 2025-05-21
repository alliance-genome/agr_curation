package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ontology.OntologyTermClosureDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.OntologyTermClosureCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.services.ontology.OntologyTermClosureService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OntologyTermClosureCrudController extends BaseEntityCrudController<OntologyTermClosureService, OntologyTermClosure, OntologyTermClosureDAO> implements OntologyTermClosureCrudInterface {

	@Inject
	OntologyTermClosureService ontologyTermClosureService;

	@Override
	@PostConstruct
	public void init() {
		setService(ontologyTermClosureService);
	}

}
