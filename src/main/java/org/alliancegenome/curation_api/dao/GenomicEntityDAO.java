package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;

@ApplicationScoped
public class GenomicEntityDAO extends BaseSQLDAO<GenomicEntity> {

    protected GenomicEntityDAO() {
        super(GenomicEntity.class);
    }

}
