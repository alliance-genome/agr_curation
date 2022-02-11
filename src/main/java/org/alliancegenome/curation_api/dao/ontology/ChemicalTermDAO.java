package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChemicalTermDAO extends BaseSQLDAO<ChemicalTerm> {

    protected ChemicalTermDAO() {
        super(ChemicalTerm.class);
    }
    
}