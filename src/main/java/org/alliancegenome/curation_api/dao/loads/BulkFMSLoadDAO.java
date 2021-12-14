package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;

@ApplicationScoped
public class BulkFMSLoadDAO extends BaseSQLDAO<BulkFMSLoad> {
    protected BulkFMSLoadDAO() {
        super(BulkFMSLoad.class);
    }
}
