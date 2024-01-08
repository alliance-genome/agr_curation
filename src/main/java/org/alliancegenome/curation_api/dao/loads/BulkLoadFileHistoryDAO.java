package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkLoadFileHistoryDAO extends BaseSQLDAO<BulkLoadFileHistory> {
	protected BulkLoadFileHistoryDAO() {
		super(BulkLoadFileHistory.class);
	}
}