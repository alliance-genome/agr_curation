package org.alliancegenome.curation_api.jobs.executors;

import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.ALLELE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CASSETTE;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.CONSTRUCT;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.FULL_INGEST;
import static org.alliancegenome.curation_api.enums.BackendBulkLoadType.GENE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

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
		List<String> constructsModel = List.of(
			"construct_ingest_set", "cassette_ingest_set", "transgenic_tool_ingest_set",
			"construct_cassette_association_ingest_set", "construct_genomic_entity_association_ingest_set",
			"cassette_genomic_entity_association_ingest_set", "cassette_transgenic_tool_association_ingest_set",
			"cassette_str_association_ingest_set");

		for (String ingestSet : constructsModel) {
			assertTrue(executor.loadTypeOwnsIngestSet(CONSTRUCT, ingestSet), ingestSet + " should run under CONSTRUCT");
		}
	}

	/**
	 * The point of scoping CONSTRUCT rather than treating it as a second FULL_INGEST: a
	 * construct submission must not start loading genes, alleles or disease annotations.
	 */
	@Test
	void constructDoesNotStrayOutsideItsModel() {
		assertTrue(executor.loadTypeOwnsIngestSet(CONSTRUCT, "construct_ingest_set"));
		assertFalse(executor.loadTypeOwnsIngestSet(CONSTRUCT, "gene_ingest_set"));
		assertFalse(executor.loadTypeOwnsIngestSet(CONSTRUCT, "allele_ingest_set"));
		assertFalse(executor.loadTypeOwnsIngestSet(CONSTRUCT, "disease_gene_ingest_set"));

		// FULL_INGEST does reach all of them.
		assertTrue(executor.loadTypeOwnsIngestSet(FULL_INGEST, "gene_ingest_set"));
		assertTrue(executor.loadTypeOwnsIngestSet(FULL_INGEST, "allele_ingest_set"));
	}

	/** Single type loads never reach this path; they are gated by their own equality check. */
	@Test
	void singleTypeLoadsAreNotContentDispatched() {
		assertFalse(executor.loadTypeOwnsIngestSet(CASSETTE, "cassette_ingest_set"));
		assertFalse(executor.loadTypeOwnsIngestSet(ALLELE, "allele_ingest_set"));
	}

	/**
	 * The dispatcher names ingest sets as strings, matched against CONSTRUCT_MODEL_INGEST_SETS, so a
	 * misspelt name would silently keep that executor out of a CONSTRUCT load.
	 */
	@Test
	void everySetTheDispatcherNamesIsAnIngestDtoProperty() throws IOException {
		Set<String> properties = new HashSet<>();
		for (Field field : IngestDTO.class.getDeclaredFields()) {
			JsonProperty property = field.getAnnotation(JsonProperty.class);
			if (property != null) {
				properties.add(property.value());
			}
		}

		String dispatcher = Files.readString(Path.of("src/main/java/org/alliancegenome/curation_api/jobs/executors/BulkLoadJobExecutor.java"));
		Matcher names = Pattern.compile("\"(\\w+_ingest_set)\"").matcher(dispatcher);
		int checked = 0;
		while (names.find()) {
			assertTrue(properties.contains(names.group(1)), names.group(1) + " is not an IngestDTO property");
			checked++;
		}
		assertTrue(checked > 20, "expected the dispatcher's ingest set names, found " + checked);
	}
}
