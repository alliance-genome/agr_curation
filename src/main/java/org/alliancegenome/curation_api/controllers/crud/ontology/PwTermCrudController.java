package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.PwTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.PwTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.PWTerm;
import org.alliancegenome.curation_api.services.ontology.PwTermService;

@RequestScoped
public class PwTermCrudController extends BaseOntologyTermController<PwTermService, PWTerm, PwTermDAO> implements PwTermCrudInterface {

	@Inject
	PwTermService pwTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(pwTermService, PWTerm.class);
	}

}
