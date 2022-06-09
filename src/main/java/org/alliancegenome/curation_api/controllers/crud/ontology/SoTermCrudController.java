package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.SoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.services.ontology.SoTermService;

@RequestScoped
public class SoTermCrudController extends BaseOntologyTermController<SoTermService, SOTerm, SoTermDAO> implements SoTermCrudInterface {

	@Inject SoTermService soTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(soTermService, SOTerm.class);
	}

}
