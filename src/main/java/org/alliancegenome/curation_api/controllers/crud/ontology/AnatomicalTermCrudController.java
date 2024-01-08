package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.AnatomicalTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.services.ontology.AnatomicalTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AnatomicalTermCrudController extends BaseOntologyTermController<AnatomicalTermService, AnatomicalTerm, AnatomicalTermDAO> implements AnatomicalTermCrudInterface {

	@Inject
	AnatomicalTermService anatomicalTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(anatomicalTermService, AnatomicalTerm.class);
	}

}
