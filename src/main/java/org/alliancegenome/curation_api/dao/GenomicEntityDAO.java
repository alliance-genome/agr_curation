package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenomicEntityDAO extends BaseSQLDAO<GenomicEntity> {

	protected GenomicEntityDAO() {
		super(GenomicEntity.class);
	}

}
