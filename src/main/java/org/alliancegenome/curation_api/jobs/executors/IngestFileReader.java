package org.alliancegenome.curation_api.jobs.executors;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Parses an ingest submission once so every executor of the load shares the one IngestDTO.
 *
 * Each executor used to call readIngestFile itself, and readIngestFile always builds every
 * ingest set in the file, not only the caller's, so a FULL_INGEST parsed the whole document
 * up to eighteen times. Sharing does not raise peak memory: each executor already held the
 * whole file's IngestDTO for its entire run, so one shared copy is the same peak without the
 * repeated parses.
 */
@ApplicationScoped
public class IngestFileReader extends LoadFileExecutor {

	/** @return the parsed submission, or null when it could not be read (the load is failed) */
	public IngestDTO read(BulkLoadFileHistory bulkLoadFileHistory) {
		return readIngestFile(bulkLoadFileHistory);
	}
}
