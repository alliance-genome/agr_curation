package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;

@ApplicationScoped
public class SoTermDAO extends BaseSQLDAO<SOTerm> {

    protected SoTermDAO() {
        super(SOTerm.class);
    }

}
