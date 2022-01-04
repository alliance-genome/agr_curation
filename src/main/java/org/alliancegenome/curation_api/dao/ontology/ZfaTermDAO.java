package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;

@ApplicationScoped
public class ZfaTermDAO extends BaseSQLDAO<ZfaTerm> {

    protected ZfaTermDAO() {
        super(ZfaTerm.class);
    }

}
