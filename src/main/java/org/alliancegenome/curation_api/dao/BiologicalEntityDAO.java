package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.*;

@ApplicationScoped
public class BiologicalEntityDAO extends BaseSQLDAO<BiologicalEntity> {

    protected BiologicalEntityDAO() {
        super(BiologicalEntity.class);
    }
    
    public BiologicalEntity getByIdOrCurie(String id) {
        return find(id);
    }

}
