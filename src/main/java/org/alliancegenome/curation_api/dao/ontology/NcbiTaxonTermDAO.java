package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;

@ApplicationScoped
public class NcbiTaxonTermDAO extends BaseSQLDAO<NCBITaxonTerm> {

    protected NcbiTaxonTermDAO() {
        super(NCBITaxonTerm.class);
    }

}
