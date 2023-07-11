package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;

@ApplicationScoped
public class BulkURLLoadDAO extends BaseSQLDAO<BulkURLLoad> {
	protected BulkURLLoadDAO() {
		super(BulkURLLoad.class);
	}
}
