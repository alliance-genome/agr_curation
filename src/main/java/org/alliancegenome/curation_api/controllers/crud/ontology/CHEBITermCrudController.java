package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.CHEBITermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;

@RequestScoped
public class CHEBITermCrudController extends BaseOntologyTermController<CHEBITermService, CHEBITerm, CHEBITermDAO> implements CHEBITermCrudInterface {
	@Inject
	CHEBITermService chebiTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(chebiTermService, CHEBITerm.class);
	}

}
