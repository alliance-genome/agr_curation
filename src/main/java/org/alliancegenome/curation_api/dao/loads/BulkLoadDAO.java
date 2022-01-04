package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;

@ApplicationScoped
public class BulkLoadDAO extends BaseSQLDAO<BulkLoad> {
    protected BulkLoadDAO() {
        super(BulkLoad.class);
    }
}
