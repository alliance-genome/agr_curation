package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkURLLoadDAO extends BaseSQLDAO<BulkURLLoad> {
	protected BulkURLLoadDAO() {
		super(BulkURLLoad.class);
	}
}
