package org.alliancegenome.curation_api.jobs.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.junit.jupiter.api.Test;

/**
 * Pins the order of the checks in LoadFileExecutor.execLoad: an executor whose ingest set the file
 * does not carry must never be schema checked, or a FULL_INGEST would fail on the version range of
 * a DTO it has nothing to load for.
 */
class LoadFileExecutorExecLoadTest {

	/** Records what execLoad does instead of touching the database. */
	static class RecordingExecutor extends LoadFileExecutor {
		final List<String> calls = new ArrayList<>();
		boolean schemaValid = true;

		@Override
		protected List<?> getIngestSet(IngestDTO ingestDto) {
			return ingestDto.getCassetteIngestSet();
		}

		@Override
		protected Class<?> getIngestDtoClass() {
			return CassetteDTO.class;
		}

		@Override
		protected boolean checkSchemaVersion(BulkLoadFileHistory bulkLoadFileHistory, Class<?> dtoClass) {
			calls.add("schema " + dtoClass.getSimpleName());
			return schemaValid;
		}

		@Override
		protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {
			calls.add("load");
		}
	}

	@Test
	void anAbsentOrEmptySetIsNeitherSchemaCheckedNorLoaded() {
		RecordingExecutor executor = new RecordingExecutor();

		executor.execLoad(new BulkLoadFileHistory(), new IngestDTO(), false);
		IngestDTO empty = new IngestDTO();
		empty.setCassetteIngestSet(new ArrayList<>());
		executor.execLoad(new BulkLoadFileHistory(), empty, false);

		assertEquals(List.of(), executor.calls);
	}

	@Test
	void aPresentSetIsSchemaCheckedAgainstTheExecutorsDtoThenLoaded() {
		RecordingExecutor executor = new RecordingExecutor();
		IngestDTO ingestDto = new IngestDTO();
		ingestDto.setCassetteIngestSet(List.of(new CassetteDTO()));

		executor.execLoad(new BulkLoadFileHistory(), ingestDto, false);

		assertEquals(List.of("schema CassetteDTO", "load"), executor.calls);
	}

	@Test
	void aFailedSchemaCheckStopsTheLoad() {
		RecordingExecutor executor = new RecordingExecutor();
		executor.schemaValid = false;
		IngestDTO ingestDto = new IngestDTO();
		ingestDto.setCassetteIngestSet(List.of(new CassetteDTO()));

		executor.execLoad(new BulkLoadFileHistory(), ingestDto, false);

		assertEquals(List.of("schema CassetteDTO"), executor.calls);
	}
}
