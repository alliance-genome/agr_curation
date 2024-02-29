package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.GenoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.GenoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.GENOTerm;
import org.alliancegenome.curation_api.services.ontology.GenoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;


public class GenoTermCrudController extends BaseOntologyTermController<GenoTermService, GENOTerm, GenoTermDAO> implements GenoTermCrudInterface {
 
	@Inject
	GenoTermService genoTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(genoTermService, GENOTerm.class);
	}
}
