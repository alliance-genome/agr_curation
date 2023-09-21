package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.CHEBITermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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
