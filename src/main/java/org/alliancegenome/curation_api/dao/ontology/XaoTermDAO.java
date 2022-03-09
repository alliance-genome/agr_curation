package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XAOTerm;

@ApplicationScoped
public class XaoTermDAO extends BaseSQLDAO<XAOTerm> {

    protected XaoTermDAO() {
        super(XAOTerm.class);
    }

}
