package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.StageTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.StageTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
import org.alliancegenome.curation_api.services.ontology.StageTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class StageTermCrudController extends BaseOntologyTermController<StageTermService, StageTerm, StageTermDAO> implements StageTermCrudInterface {

	@Inject
	StageTermService stageTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(stageTermService, StageTerm.class);
	}

}
