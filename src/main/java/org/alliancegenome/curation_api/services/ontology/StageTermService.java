package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.StageTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class StageTermService extends BaseOntologyTermService<StageTerm, StageTermDAO> {

	@Inject
	StageTermDAO stageTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(stageTermDAO);
	}

}
