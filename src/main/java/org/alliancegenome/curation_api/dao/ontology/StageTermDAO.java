package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StageTermDAO extends BaseSQLDAO<StageTerm> {

	protected StageTermDAO() {
		super(StageTerm.class);
	}

}