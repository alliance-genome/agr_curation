package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;

@ApplicationScoped
public class EcoTermDAO extends BaseSQLDAO<EcoTerm> {
    
    protected EcoTermDAO() {
        super(EcoTerm.class);
    }
    
}