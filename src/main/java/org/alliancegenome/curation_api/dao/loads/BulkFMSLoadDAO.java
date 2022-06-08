package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;

@ApplicationScoped
public class BulkFMSLoadDAO extends BaseSQLDAO<BulkFMSLoad> {
	protected BulkFMSLoadDAO() {
		super(BulkFMSLoad.class);
	}
}
