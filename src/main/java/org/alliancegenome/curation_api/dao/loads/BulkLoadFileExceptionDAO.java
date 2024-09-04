package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class BulkLoadFileExceptionDAO extends BaseSQLDAO<BulkLoadFileException> {
	protected BulkLoadFileExceptionDAO() {
		super(BulkLoadFileException.class);
	}

	public void cleanUpTwoWeekOldExceptions() {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'");
		jpqlQuery.executeUpdate();
	}
}