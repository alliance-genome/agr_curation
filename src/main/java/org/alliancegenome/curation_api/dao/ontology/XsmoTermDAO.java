package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;

@ApplicationScoped
public class XsmoTermDAO extends BaseSQLDAO<XSMOTerm> {

    protected XsmoTermDAO() {
        super(XSMOTerm.class);
    }

}
