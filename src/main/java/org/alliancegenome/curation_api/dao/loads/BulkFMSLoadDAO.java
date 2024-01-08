package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkFMSLoadDAO extends BaseSQLDAO<BulkFMSLoad> {
	protected BulkFMSLoadDAO() {
		super(BulkFMSLoad.class);
	}
}
