package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ExperimentalConditionOntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.services.ontology.ExperimentalConditionOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExperimentalConditionOntologyTermCrudController
	extends BaseOntologyTermController<ExperimentalConditionOntologyTermService, ExperimentalConditionOntologyTerm, ExperimentalConditionOntologyTermDAO>
	implements ExperimentalConditionOntologyTermCrudInterface {

	@Inject
	ExperimentalConditionOntologyTermService experimentalConditionOntologyTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(experimentalConditionOntologyTermService, ExperimentalConditionOntologyTerm.class);
	}

}
