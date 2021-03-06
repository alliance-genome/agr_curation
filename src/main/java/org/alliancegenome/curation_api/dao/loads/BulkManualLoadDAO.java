package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;

@ApplicationScoped
public class BulkManualLoadDAO extends BaseSQLDAO<BulkManualLoad> {
	protected BulkManualLoadDAO() {
		super(BulkManualLoad.class);
	}
}
