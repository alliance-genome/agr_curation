package org.alliancegenome.curation_api.dao;


import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneToGeneParalogyDAO extends BaseSQLDAO<GeneToGeneParalogy> {

    protected GeneToGeneParalogyDAO() {
        super(GeneToGeneParalogy.class);
    }
    
}
