package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;

@ApplicationScoped
public class MpTermDAO extends BaseSQLDAO<MPTerm> {

    protected MpTermDAO() {
        super(MPTerm.class);
    }

}
