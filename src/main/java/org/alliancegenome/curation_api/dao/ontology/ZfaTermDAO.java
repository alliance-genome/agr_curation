package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZfaTermDAO extends BaseSQLDAO<ZfaTerm> {

    protected ZfaTermDAO() {
        super(ZfaTerm.class);
    }

}
