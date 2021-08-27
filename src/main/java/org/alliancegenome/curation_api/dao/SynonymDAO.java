package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Synonym;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SynonymDAO extends BaseSQLDAO<Synonym> {

    protected SynonymDAO() {
        super(Synonym.class);
    }

}
