package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExperimentalConditionOntologyTermDAO extends BaseSQLDAO<ExperimentalConditionOntologyTerm> {

	protected ExperimentalConditionOntologyTermDAO() {
		super(ExperimentalConditionOntologyTerm.class);
	}

}