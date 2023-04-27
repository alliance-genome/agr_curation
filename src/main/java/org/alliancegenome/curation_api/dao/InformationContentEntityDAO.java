package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InformationContentEntityDAO extends BaseSQLDAO<InformationContentEntity> {

	protected InformationContentEntityDAO() {
		super(InformationContentEntity.class);
	}

}
