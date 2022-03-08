package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBdvTerm;

@ApplicationScoped
public class FbdvTermDAO extends BaseSQLDAO<FBdvTerm> {

    protected FbdvTermDAO() {
        super(FBdvTerm.class);
    }

}
