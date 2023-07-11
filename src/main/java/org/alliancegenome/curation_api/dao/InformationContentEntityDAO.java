package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;

@ApplicationScoped
public class InformationContentEntityDAO extends BaseSQLDAO<InformationContentEntity> {

	protected InformationContentEntityDAO() {
		super(InformationContentEntity.class);
	}

}
