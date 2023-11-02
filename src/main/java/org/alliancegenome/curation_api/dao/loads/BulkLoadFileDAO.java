package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BulkLoadFileDAO extends BaseSQLDAO<BulkLoadFile> {
	protected BulkLoadFileDAO() {
		super(BulkLoadFile.class);
	}
}