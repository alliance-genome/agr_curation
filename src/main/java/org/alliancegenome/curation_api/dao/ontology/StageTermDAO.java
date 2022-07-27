package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;

@ApplicationScoped
public class StageTermDAO extends BaseSQLDAO<StageTerm> {

	protected StageTermDAO() {
		super(StageTerm.class);
	}
	
}