package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkLoadDAO extends BaseSQLDAO<BulkLoad> {
	protected BulkLoadDAO() {
		super(BulkLoad.class);
	}
}
