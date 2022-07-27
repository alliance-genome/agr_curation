package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ExperimentalConditionOntologyTermService extends BaseOntologyTermService<ExperimentalConditionOntologyTerm, ExperimentalConditionOntologyTermDAO> {

	@Inject ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(experimentalConditionOntologyTermDAO);
	}
	
}
