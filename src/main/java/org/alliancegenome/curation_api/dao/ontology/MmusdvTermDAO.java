package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MmusDvTerm;

@ApplicationScoped
public class MmusdvTermDAO extends BaseSQLDAO<MmusDvTerm> {

    protected MmusdvTermDAO() {
        super(MmusDvTerm.class);
    }

}
