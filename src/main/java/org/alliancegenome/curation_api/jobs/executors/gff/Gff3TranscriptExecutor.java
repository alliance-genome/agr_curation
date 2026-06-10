package org.alliancegenome.curation_api.jobs.executors.gff;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.alliancegenome.curation_api.services.TranscriptService;
import org.alliancegenome.curation_api.services.associations.TranscriptGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.TranscriptGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.Gff3AttributesHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3TranscriptExecutor extends Gff3Executor {

	@Inject
	TranscriptService transcriptService;
	@Inject
	TranscriptGenomicLocationAssociationService transcriptLocationService;
	@Inject
	TranscriptGeneAssociationService transcriptGeneService;
	@Inject
	SpeciesService speciesService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws Exception {
		try {
			CsvSchema gff3Schema = CsvSchemaBuilder.gff3Schema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<Gff3DTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(Gff3DTO.class).with(gff3Schema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
			Log.info("Loading GFF Data into Memory");
			List<Gff3DTO> gffRawData = it.readAll();
			Log.info("Finished Loading GFF Data into Memory");
			List<String> gffHeaderData = new ArrayList<>();
			List<Gff3DTO> gffFileData = new ArrayList<>();
			ProcessDisplayHelper ph = new ProcessDisplayHelper();
			ph.startProcess("GFF Transcript header pre-processing", gffRawData.size());
			for (Gff3DTO gffLine : gffRawData) {
				if (gffLine.getSeqId().startsWith("#")) {
					gffHeaderData.add(gffLine.getSeqId());
				} else {
					gffFileData.add(gffLine);
				}
				ph.progressProcess();
			}
			ph.finishProcess();
			gffRawData.clear();

			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
			Species species = speciesService.getByDisplayName(fmsLoad.getFmsDataSubType());

			List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedTranscriptGffData = Gff3AttributesHelper.getTranscriptGffData(gffFileData, species);
			Map<String, String> geneIdCurieMap = gff3Service.getGeneIdCurieMap(gffFileData, species);

			gffFileData.clear();

			List<Long> entityIdsAdded = new ArrayList<>();
			List<Long> locationIdsAdded = new ArrayList<>();
			List<Long> associationIdsAdded = new ArrayList<>();

			String assemblyId = loadGenomeAssemblyFromGFF(bulkLoadFileHistory, gffHeaderData, species);

			boolean success = runLoad(bulkLoadFileHistory, gffHeaderData, preProcessedTranscriptGffData, geneIdCurieMap, entityIdsAdded, locationIdsAdded, associationIdsAdded, species, assemblyId);
			if (success) {
				runCleanup(transcriptLocationService, bulkLoadFileHistory, species.getDisplayName(), transcriptLocationService.getIdsByDataProvider(species), locationIdsAdded, "GFF transcript genomic location association");
				runCleanup(transcriptGeneService, bulkLoadFileHistory, species.getDisplayName(), transcriptGeneService.getIdsByDataProvider(species), associationIdsAdded, "GFF transcript gene association");
				runCleanup(transcriptService, bulkLoadFileHistory, species.getDisplayName(), transcriptService.getIdsByDataProvider(species), entityIdsAdded, "GFF transcript");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
		}
	}

	private boolean runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData, Map<String, String> geneIdCurieMap, List<Long> entityIdsAdded, List<Long> locationIdsAdded, List<Long> associationIdsAdded,
			Species species, String assemblyId) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF Transcript update for " + species.getDisplayName(), gffData.size());

		history.setCount("Entities", gffData.size());
		history.setCount("Locations", gffData.size());
		history.setCount("Associations", gffData.size());
		updateHistory(history);

		int batchSize = 3000;
		for (int i = 0; i < gffData.size(); i += batchSize) {
			int end = Math.min(i + batchSize, gffData.size());
			List<ImmutablePair<Gff3DTO, Map<String, String>>> batch = gffData.subList(i, end);

			gff3Service.loadTranscriptBatch(batch, entityIdsAdded, locationIdsAdded, associationIdsAdded, species, assemblyId, geneIdCurieMap, history, ph);

			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
		updateHistory(history);
		ph.finishProcess();
		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, String assemblyName, List<Gff3DTO> gffData) {
		List<Long> idsAdded = new ArrayList<>();
		Species species = speciesService.getByDisplayName(dataProviderName);
		List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedTranscriptGffData = Gff3AttributesHelper.getTranscriptGffData(gffData, species);
		Map<String, String> geneIdCurieMap = gff3Service.getGeneIdCurieMap(gffData, species);
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, null, preProcessedTranscriptGffData, geneIdCurieMap, idsAdded, idsAdded, idsAdded, species, assemblyName);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

}
