package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;

@ApplicationScoped
public class CrossReferenceDAO extends BaseSQLDAO<CrossReference> {

    protected CrossReferenceDAO(){ super(CrossReference.class); }
}
