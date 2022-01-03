package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Gene;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

    protected GeneDAO() {
        super(Gene.class);
    }
    
    public Gene getByIdOrCurie(String id) {
        return find(id);
    }

}
