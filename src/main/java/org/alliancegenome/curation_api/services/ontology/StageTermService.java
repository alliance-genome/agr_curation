package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.StageTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class StageTermService extends BaseOntologyTermService<StageTerm, StageTermDAO> {

	@Inject StageTermDAO stageTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(stageTermDAO);
	}
	
}
