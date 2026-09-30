package org.alliancegenome.curation_api.jobs.executors;

import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CASSETTE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.FULL_INGEST;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Pins which executors a content dispatched load reaches.
 *
 * The constructs model arrives as a family of files sharing one load type, so CONSTRUCT has to
 * fan out the way FULL_INGEST does. Getting the scope wrong is silent in both directions: too
 * narrow and a submitted ingest set is skipped while the load still reports success, too wide
 * and a CONSTRUCT submission starts running gene or allele executors over a file that has
 * nothing for them.
 */
class BulkLoadJobExecutorFanOutTest {

	private final BulkLoadJobExecutor executor = new BulkLoadJobExecutor();

	@Test
	void constructAndFullIngestAreTheContentDispatchedTypes() {
		assertTrue(executor.fansOut(FULL_INGEST));
		assertTrue(executor.fansOut(CONSTRUCT));
		assertFalse(executor.fansOut(CASSETTE), "single type loads keep their own explicit check");
		assertFalse(executor.fansOut(GENE));
	}

	/** A CONSTRUCT_<MOD> submission has to reach every entity in the constructs model. */
	@Test
	void constructReachesTheWholeConstructsModel() {
		Set<String> present = Set.of(
			"construct_ingest_set", "cassette_ingest_set", "transgenic_tool_ingest_set",
			"construct_cassette_association_ingest_set", "construct_genomic_entity_association_ingest_set",
			"cassette_genomic_entity_association_ingest_set", "cassette_transgenic_tool_association_ingest_set",
			"cassette_str_association_ingest_set");

		for (String ingestSet : present) {
			assertTrue(executor.fileCarriesIngestSet(CONSTRUCT, present, ingestSet), ingestSet + " should run under CONSTRUCT");
		}
	}

	/**
	 * The point of scoping CONSTRUCT rather than treating it as a second FULL_INGEST: a
	 * construct submission must not start loading genes, alleles or disease annotations.
	 */
	@Test
	void constructDoesNotStrayOutsideItsModel() {
		Set<String> present = Set.of("construct_ingest_set", "gene_ingest_set", "allele_ingest_set", "disease_gene_ingest_set");

		assertTrue(executor.fileCarriesIngestSet(CONSTRUCT, present, "construct_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(CONSTRUCT, present, "gene_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(CONSTRUCT, present, "allele_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(CONSTRUCT, present, "disease_gene_ingest_set"));

		// The same file under FULL_INGEST does reach all of them.
		assertTrue(executor.fileCarriesIngestSet(FULL_INGEST, present, "gene_ingest_set"));
		assertTrue(executor.fileCarriesIngestSet(FULL_INGEST, present, "allele_ingest_set"));
	}

	/** An ingest set the file does not carry must not schedule its executor. */
	@Test
	void absentIngestSetsAreSkipped() {
		Set<String> present = Set.of("cassette_ingest_set");

		assertTrue(executor.fileCarriesIngestSet(CONSTRUCT, present, "cassette_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(CONSTRUCT, present, "construct_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(FULL_INGEST, present, "gene_ingest_set"));
	}

	/**
	 * A failed scan must degrade to the behaviour that predates it, running everything the load
	 * type owns, rather than silently loading nothing.
	 */
	@Test
	void aFailedScanRunsEverythingTheLoadTypeOwns() {
		assertTrue(executor.fileCarriesIngestSet(FULL_INGEST, null, "gene_ingest_set"));
		assertTrue(executor.fileCarriesIngestSet(CONSTRUCT, null, "cassette_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(CONSTRUCT, null, "gene_ingest_set"), "scope still applies without a scan");
	}

	/** Single type loads never reach this path; they are gated by their own equality check. */
	@Test
	void singleTypeLoadsAreNotContentDispatched() {
		Set<String> present = Set.of("cassette_ingest_set", "gene_ingest_set");

		assertFalse(executor.fileCarriesIngestSet(CASSETTE, present, "cassette_ingest_set"));
		assertFalse(executor.fileCarriesIngestSet(ALLELE, present, "allele_ingest_set"));
	}
}
