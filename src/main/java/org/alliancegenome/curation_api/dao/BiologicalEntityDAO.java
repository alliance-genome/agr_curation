package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;

@ApplicationScoped
public class BiologicalEntityDAO extends BaseSQLDAO<BiologicalEntity> {

    protected BiologicalEntityDAO() {
        super(BiologicalEntity.class);
    }

}
