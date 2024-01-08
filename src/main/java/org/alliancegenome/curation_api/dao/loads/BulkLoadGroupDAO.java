package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkLoadGroupDAO extends BaseSQLDAO<BulkLoadGroup> {
	protected BulkLoadGroupDAO() {
		super(BulkLoadGroup.class);
	}
}
