package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;

@ApplicationScoped
public class EmapaTermDAO extends BaseSQLDAO<EMAPATerm> {

    protected EmapaTermDAO() {
        super(EMAPATerm.class);
    }

}
