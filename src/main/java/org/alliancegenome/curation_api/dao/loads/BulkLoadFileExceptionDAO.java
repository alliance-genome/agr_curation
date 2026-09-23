package org.alliancegenome.curation_api.dao.loads;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.Stream;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.hibernate.jpa.HibernateHints;

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

	/**
	 * SCRUM-6582: write a history's exceptions to the writer as a JSON array without loading entities.
	 *
	 * Postgres builds each element from the jsonb column, and jsonObject is already JSON text, so
	 * nothing is parsed or serialized in Java. Rows are fetched in batches, so memory stays flat for
	 * histories with hundreds of thousands of exceptions. Null message/messages keys are omitted.
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public void writeExceptionsAsJsonArray(Long historyId, Writer writer) throws IOException {
		String sql = """
				SELECT '{'
					|| COALESCE('"message":' || CAST(to_json(x.message) AS text) || ',', '')
					|| COALESCE('"messages":' || CAST(x.messages AS text) || ',', '')
					|| '"jsonObject":' || COALESCE(x."jsonObject", '{}')
					|| '}'
				FROM BulkLoadFileException e, jsonb_to_record(e.exception) AS x(message text, messages jsonb, "jsonObject" text)
				WHERE e.bulkLoadFileHistory_id = :historyId
				""";

		Query query = entityManager.createNativeQuery(sql, String.class)
			.setParameter("historyId", historyId)
			.setHint(HibernateHints.HINT_FETCH_SIZE, 1000);
		try (Stream<String> rows = query.getResultStream()) {
			Iterator<String> iterator = rows.iterator();
			String separator = "\n";
			writer.write("[");
			while (iterator.hasNext()) {
				writer.write(separator);
				writer.write(iterator.next());
				separator = ",\n";
			}
			writer.write("\n]");
		}
	}

	@Transactional
	public void cleanUpTwoWeekOldExceptions() {
		Log.info("Deleting Old Bulk Exceptions: \"DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'\"");
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM BulkLoadFileException WHERE dbdatecreated < NOW() - INTERVAL '14 days'");
		jpqlQuery.executeUpdate();
	}
}