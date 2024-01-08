package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkManualLoadDAO extends BaseSQLDAO<BulkManualLoad> {
	protected BulkManualLoadDAO() {
		super(BulkManualLoad.class);
	}
}
