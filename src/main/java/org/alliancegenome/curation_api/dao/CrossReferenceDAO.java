package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CrossReferenceDAO extends BaseSQLDAO<CrossReference> {

    protected CrossReferenceDAO(){ super(CrossReference.class); }
}
