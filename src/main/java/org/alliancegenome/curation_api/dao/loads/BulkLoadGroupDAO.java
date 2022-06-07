package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;

@ApplicationScoped
public class BulkLoadGroupDAO extends BaseSQLDAO<BulkLoadGroup> {
	protected BulkLoadGroupDAO() {
		super(BulkLoadGroup.class);
	}
}
