package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;

@ApplicationScoped
public class CHEBITermDAO extends BaseSQLDAO<CHEBITerm> {
    protected CHEBITermDAO() {
        super(CHEBITerm.class);
    }
}
