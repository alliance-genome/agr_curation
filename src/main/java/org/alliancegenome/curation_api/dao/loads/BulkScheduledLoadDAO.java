package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkScheduledLoad;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkScheduledLoadDAO extends BaseSQLDAO<BulkScheduledLoad> {
	protected BulkScheduledLoadDAO() {
		super(BulkScheduledLoad.class);
	}
}
