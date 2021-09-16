package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;

@ApplicationScoped
public class XcoTermDAO extends BaseSQLDAO<XcoTerm> {

    protected XcoTermDAO() {
        super(XcoTerm.class);
    }

}
