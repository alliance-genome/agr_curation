package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BiologicalEntityDAO extends BaseSQLDAO<BiologicalEntity> {

	protected BiologicalEntityDAO() {
		super(BiologicalEntity.class);
	}

}
