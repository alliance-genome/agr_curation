package org.alliancegenome.curation_api.dao.loads;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BulkLoadFileExceptionDAO extends BaseSQLDAO<BulkLoadFileException> {
	protected BulkLoadFileExceptionDAO() {
		super(BulkLoadFileException.class);
	}

	/**
	 * SCRUM-6258: persist a load exception in its own transaction so it survives a rollback of
	 * the batch that produced it.
	 *
	 * The GFF batch loaders record failures with the batch's own transaction active. When that
	 * batch then fails to commit - as the ZFIN GFF loads did on the duplicate MT assembly
	 * component - the exception rows roll back alongside the data, and every record-level
	 * reason for the failure is lost. Beta showed 131,099 failed associations with zero
	 * BulkLoadFileException rows for the same history.
	 *
	 * BulkLoadFileException has a plain @ManyToOne to BulkLoadFileHistory with no JPA cascade,
	 * so writing the FK from a history that is detached in this new transaction is safe.
	 */
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void persistInNewTransaction(BulkLoadFileException exception) {
		persist(exception);
	}

	@Transactional
	public void cleanUpTwoWeekOldExceptions() {
		Log.info("Deleting Old Bulk Exceptions: \"DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'\"");
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'");
		jpqlQuery.executeUpdate();
	}
}