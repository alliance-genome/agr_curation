package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ExperimentalConditionOntologyTermService extends BaseOntologyTermService<ExperimentalConditionOntologyTerm, ExperimentalConditionOntologyTermDAO> {

	@Inject
	ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(experimentalConditionOntologyTermDAO);
	}

}
