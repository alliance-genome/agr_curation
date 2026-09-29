package org.alliancegenome.curation_api.jobs.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pins the scan that decides which executors a FULL_INGEST actually needs to run.
 *
 * Worth pinning because both failure directions are silent. Miss a key that is present and
 * the executor never runs, so the data is dropped with a successful load; report a key that
 * is absent and the saving evaporates back into a full parse.
 */
class IngestFileScannerTest {

	@TempDir Path tempDir;

	private IngestFileScanner scanner;

	@BeforeEach
	void setUp() {
		scanner = new IngestFileScanner();
		scanner.mapper = new ObjectMapper();
	}

	private String gzip(String json) throws IOException {
		Path file = tempDir.resolve("ingest.json.gz");
		try (Writer writer = new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(file)))) {
			writer.write(json);
		}
		return file.toString();
	}

	/**
	 * The shape this was written for: a single entity submission. Only the one executor
	 * should run, and the seventeen others must be skipped rather than each re-parsing the
	 * whole document.
	 */
	@Test
	void reportsOnlyTheIngestSetTheFileCarries() throws IOException {
		String path = gzip("""
			{
				"alliance_member_release_version": "2026_03",
				"cassette_ingest_set": [
					{ "primary_external_id": "FB:FBtp0000001_cas", "internal": false },
					{ "primary_external_id": "FB:FBtp0000002_cas", "internal": false }
				],
				"linkml_version": "2.18.0"
			}
			""");

		assertEquals(Set.of("cassette_ingest_set"), scanner.presentIngestSets(path));
	}

	/**
	 * An executor bails on an empty ingest set, so reporting the key would schedule a parse
	 * that can only return early.
	 */
	@Test
	void treatsAnEmptyIngestSetAsAbsent() throws IOException {
		String path = gzip("""
			{
				"gene_ingest_set": [],
				"cassette_ingest_set": [ { "primary_external_id": "FB:FBtp0000001_cas" } ]
			}
			""");

		assertEquals(Set.of("cassette_ingest_set"), scanner.presentIngestSets(path));
	}

	/**
	 * A real FULL_INGEST carries several sets, and nested objects and arrays inside the
	 * records must not be mistaken for top level fields.
	 */
	@Test
	void findsEveryPopulatedSetAndIgnoresNesting() throws IOException {
		String path = gzip("""
			{
				"gene_ingest_set": [
					{ "gene_symbol_dto": { "display_text": "x", "evidence_curies": ["PMID:1"] } }
				],
				"construct_ingest_set": [ { "primary_external_id": "FB:FBtp0000003" } ],
				"agm_sequence_targeting_reagent_association_ingest_set": [ { "agm_subject_identifier": "FB:FBal0000001" } ],
				"linkml_version": "2.18.0"
			}
			""");

		assertEquals(
			Set.of("gene_ingest_set", "construct_ingest_set", "agm_sequence_targeting_reagent_association_ingest_set"),
			scanner.presentIngestSets(path));
	}

	/**
	 * The name the AGM/STR association executor needs is the long form, not the agm_str_
	 * abbreviation its Java field uses. Getting this wrong would silently skip that load.
	 */
	@Test
	void usesTheJsonNameNotTheJavaFieldName() throws IOException {
		String path = gzip("""
			{ "agm_sequence_targeting_reagent_association_ingest_set": [ { "a": 1 } ] }
			""");

		Set<String> present = scanner.presentIngestSets(path);
		assertTrue(present.contains("agm_sequence_targeting_reagent_association_ingest_set"));
		assertTrue(present.stream().noneMatch(name -> name.startsWith("agm_str")));
	}

	/**
	 * A file with nothing loadable must come back empty rather than throwing, so the caller
	 * can fail the load with a clear message instead of running eighteen no-op executors.
	 */
	@Test
	void returnsEmptyWhenThereIsNothingToLoad() throws IOException {
		String path = gzip("""
			{ "alliance_member_release_version": "2026_03", "linkml_version": "2.18.0" }
			""");

		assertTrue(scanner.presentIngestSets(path).isEmpty());
	}
}
