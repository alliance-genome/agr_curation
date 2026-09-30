package org.alliancegenome.curation_api.jobs.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAlleleAssociationDTO;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

class IngestFileReaderTest {

	@Test
	void reportsOnlyTheSetsThatHoldRecords() {
		IngestDTO ingestDto = new IngestDTO();
		ingestDto.setCassetteIngestSet(List.of(new CassetteDTO()));
		ingestDto.setAgmAlleleAssociationIngestSet(List.of(new AgmAlleleAssociationDTO()));

		assertEquals(Set.of("cassette_ingest_set", "agm_allele_association_ingest_set"), IngestFileReader.presentIngestSets(ingestDto));
	}

	/** An executor bails on an empty ingest set, so reporting it would dispatch for nothing. */
	@Test
	void treatsAnEmptyOrMissingSetAsAbsent() {
		IngestDTO ingestDto = new IngestDTO();
		ingestDto.setGeneIngestSet(new ArrayList<GeneDTO>());

		assertTrue(IngestFileReader.presentIngestSets(ingestDto).isEmpty());
		assertTrue(IngestFileReader.presentIngestSets(new IngestDTO()).isEmpty());
	}

	/**
	 * The fan-out matches the set names BulkLoadJobExecutor passes as strings against the
	 * IngestDTO property names, so a misspelt name there would silently skip that executor.
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
