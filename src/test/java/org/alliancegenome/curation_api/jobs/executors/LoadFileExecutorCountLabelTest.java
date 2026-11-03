package org.alliancegenome.curation_api.jobs.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Executors that share one load history count under labels derived from their ingest set, so a
 * construct family file shows one row per set on the data loads page rather than one "Records" row
 * that the last executor to run overwrites.
 */
class LoadFileExecutorCountLabelTest {

	@Test
	void labelIsTheIngestSetNameCapitalisedWithoutTheSuffix() {
		assertEquals("Construct", LoadFileExecutor.countLabel("construct_ingest_set"));
		assertEquals("Transgenic Tool", LoadFileExecutor.countLabel("transgenic_tool_ingest_set"));
		assertEquals("Cassette Genomic Entity Association", LoadFileExecutor.countLabel("cassette_genomic_entity_association_ingest_set"));
		assertEquals("Cassette Str Association", LoadFileExecutor.countLabel("cassette_str_association_ingest_set"));
	}
}
