package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Reports which {@code *_ingest_set} arrays an ingest file actually carries, without
 * building a single DTO.
 *
 * A FULL_INGEST fans out to every executor, and each one used to call readIngestFile and
 * deserialise the whole document just to discover its own ingest set was absent. On a
 * 330MB submission that is eighteen full parses to do one pass of useful work. Knowing the
 * keys up front lets the fan-out skip the executors with nothing to do.
 *
 * Deliberately a streaming scan rather than a cached IngestDTO: caching would keep the
 * fully materialised object graph of a 330MB file alive across the whole fan-out, trading
 * wasted CPU for heap exhaustion. This walks the top level with skipChildren() instead, so
 * it costs one gzip pass and allocates essentially nothing.
 */
@ApplicationScoped
public class IngestFileScanner {

	private static final String INGEST_SET_SUFFIX = "_ingest_set";

	@Inject ObjectMapper mapper;

	/**
	 * @param localFilePath gzipped ingest file to scan
	 * @return the names of the top level {@code *_ingest_set} fields that are present and
	 *         hold at least one element. An empty or null array counts as absent, matching
	 *         the executors, which bail on an empty ingest set.
	 */
	public Set<String> presentIngestSets(String localFilePath) throws IOException {
		Set<String> present = new HashSet<>();

		try (JsonParser parser = mapper.getFactory().createParser(new GZIPInputStream(new FileInputStream(localFilePath)))) {
			if (parser.nextToken() != JsonToken.START_OBJECT) {
				return present;
			}

			while (parser.nextToken() == JsonToken.FIELD_NAME) {
				String fieldName = parser.currentName();
				JsonToken value = parser.nextToken();

				if (value == JsonToken.START_ARRAY && fieldName.endsWith(INGEST_SET_SUFFIX)) {
					if (parser.nextToken() == JsonToken.END_ARRAY) {
						continue; // present but empty, which the executors treat as nothing to do
					}
					present.add(fieldName);
					// Positioned on the first element; step over it and everything after it
					// without materialising any of it.
					parser.skipChildren();
					while (parser.nextToken() != JsonToken.END_ARRAY) {
						parser.skipChildren();
					}
				} else if (value == JsonToken.START_ARRAY || value == JsonToken.START_OBJECT) {
					parser.skipChildren();
				}
			}
		}

		return present;
	}
}
