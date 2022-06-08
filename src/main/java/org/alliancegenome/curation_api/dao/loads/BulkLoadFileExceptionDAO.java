package org.alliancegenome.curation_api.dao.loads;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;

@ApplicationScoped
public class BulkLoadFileExceptionDAO extends BaseSQLDAO<BulkLoadFileException> {
	protected BulkLoadFileExceptionDAO() {
		super(BulkLoadFileException.class);
	}
}