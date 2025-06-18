package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkGoogleAnalyticsLoad;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkGoogleAnalyticsLoadDAO extends BaseSQLDAO<BulkGoogleAnalyticsLoad> {
	protected BulkGoogleAnalyticsLoadDAO() {
		super(BulkGoogleAnalyticsLoad.class);
	}
}
