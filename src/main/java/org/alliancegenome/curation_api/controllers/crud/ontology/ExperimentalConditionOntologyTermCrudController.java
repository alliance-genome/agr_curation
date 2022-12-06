package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ExperimentalConditionOntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.services.ontology.ExperimentalConditionOntologyTermService;

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
