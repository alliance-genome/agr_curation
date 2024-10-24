package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XsmoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XsmoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XsmoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XsmoTermCrudController extends BaseOntologyTermController<XsmoTermService, XSMOTerm, XsmoTermDAO> implements XsmoTermCrudInterface {

	@Inject
	XsmoTermService xsmoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setIgnoreEntitiesWithChebiXref(true);
		setService(xsmoTermService, XSMOTerm.class, config);
	}

}
