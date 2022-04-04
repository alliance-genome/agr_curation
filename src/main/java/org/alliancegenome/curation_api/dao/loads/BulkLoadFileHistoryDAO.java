package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;

@ApplicationScoped
public class BulkLoadFileHistoryDAO extends BaseSQLDAO<BulkLoadFileHistory> {
    protected BulkLoadFileHistoryDAO() {
        super(BulkLoadFileHistory.class);
    }
}