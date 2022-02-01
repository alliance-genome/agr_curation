package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OntologyTermDAO extends BaseSQLDAO<OntologyTerm> {

    protected OntologyTermDAO() {
        super(OntologyTerm.class);
    }
    
}