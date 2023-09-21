package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ObiTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ObiTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.ObiTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ObiTermCrudController extends BaseOntologyTermController<ObiTermService, OBITerm, ObiTermDAO> implements ObiTermCrudInterface {

	@Inject
	ObiTermService obiTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("OBI");
		setService(obiTermService, OBITerm.class, config);
	}

}
