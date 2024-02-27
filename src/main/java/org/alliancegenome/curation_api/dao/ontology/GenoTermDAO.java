package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GENOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenoTermDAO extends BaseSQLDAO<GENOTerm> {
    
    protected GenoTermDAO() {
        super(GENOTerm.class);
    }
}
