package org.alliancegenome.curation_api.jobs.executors;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	private static final String INGEST_SET_SUFFIX = "_ingest_set";

	/** @return the parsed submission, or null when it could not be read (the load is failed) */
	public IngestDTO read(BulkLoadFileHistory bulkLoadFileHistory) {
		return readIngestFile(bulkLoadFileHistory);
	}

	/**
	 * @return the JSON names of the ingest sets that hold at least one element. An empty or
	 *         null set counts as absent, matching the executors, which bail on an empty set.
	 */
	public static Set<String> presentIngestSets(IngestDTO ingestDto) {
		Set<String> present = new HashSet<>();
		for (Field field : IngestDTO.class.getDeclaredFields()) {
			JsonProperty property = field.getAnnotation(JsonProperty.class);
			if (property == null || !property.value().endsWith(INGEST_SET_SUFFIX) || !List.class.isAssignableFrom(field.getType())) {
				continue;
			}
			try {
				field.setAccessible(true);
				List<?> ingestSet = (List<?>) field.get(ingestDto);
				if (ingestSet != null && !ingestSet.isEmpty()) {
					present.add(property.value());
				}
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Cannot read " + field.getName() + " on IngestDTO", e);
			}
		}
		return present;
	}
}
