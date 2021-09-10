package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.*;

@ApplicationScoped
public class EcoTermDAO extends BaseSQLDAO<ECOTerm> {
    
    protected EcoTermDAO() {
        super(ECOTerm.class);
    }
    
}