package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XbedTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XbedTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;
import org.alliancegenome.curation_api.services.ontology.XbedTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XbedTermCrudController extends BaseOntologyTermController<XbedTermService, XBEDTerm, XbedTermDAO> implements XbedTermCrudInterface {

	@Inject
	XbedTermService xbedTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(xbedTermService, XBEDTerm.class);
	}

}
