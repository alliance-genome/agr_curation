package org.alliancegenome.curation_api.jobs.executors.gff;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.ExonService;
import org.alliancegenome.curation_api.services.associations.ExonGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.TranscriptExonAssociationService;
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
public class Gff3ExonExecutor extends Gff3Executor {

	@Inject
	ExonService exonService;
	@Inject
	ExonGenomicLocationAssociationService exonLocationService;
	@Inject
	TranscriptExonAssociationService transcriptExonService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws Exception {

		CsvSchema gff3Schema = CsvSchemaBuilder.gff3Schema();
		CsvMapper csvMapper = new CsvMapper();
		MappingIterator<Gff3DTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(Gff3DTO.class).with(gff3Schema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
		Log.info("Loading GFF Data into Memory");
		List<Gff3DTO> gffRawData = it.readAll();
		Log.info("Finished Loading GFF Data into Memory");
		List<String> gffHeaderData = new ArrayList<>();
		List<Gff3DTO> gffFileData = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("GFF Exon header pre-processing", gffRawData.size());
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
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());

		List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedExonGffData = Gff3AttributesHelper.getExonGffData(gffFileData, dataProvider);

		gffFileData.clear();

		List<Long> entityIdsAdded = new ArrayList<>();
		List<Long> locationIdsAdded = new ArrayList<>();
		List<Long> associationIdsAdded = new ArrayList<>();

		String assemblyId = loadGenomeAssemblyFromGFF(gffHeaderData);

		if (!validateGffAssemblyMatchesSpecies(bulkLoadFileHistory, dataProvider, assemblyId)) {
			return;
		}

		boolean success = runLoad(bulkLoadFileHistory, gffHeaderData, preProcessedExonGffData, entityIdsAdded, locationIdsAdded, associationIdsAdded, dataProvider, assemblyId);
		if (success) {
			runCleanup(exonLocationService, bulkLoadFileHistory, dataProvider.name(), exonLocationService.getIdsByDataProvider(dataProvider), locationIdsAdded, "GFF exon genomic location association");
			runCleanup(transcriptExonService, bulkLoadFileHistory, dataProvider.name(), transcriptExonService.getIdsByDataProvider(dataProvider), associationIdsAdded, "GFF transcript exon association");
			runCleanup(exonService, bulkLoadFileHistory, dataProvider.name(), exonService.getIdsByDataProvider(dataProvider), entityIdsAdded, "GFF exon");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);

	}

	private boolean runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData, List<Long> entityIdsAdded, List<Long> locationIdsAdded, List<Long> associationIdsAdded, BackendBulkDataProvider dataProvider,
			String assemblyId) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF Exon update for " + dataProvider.name(), gffData.size());

		history.setCount("Entities", gffData.size());
		history.setCount("Locations", gffData.size());
		history.setCount("Associations", gffData.size());
		updateHistory(history);

		int batchSize = 3000;
		for (int i = 0; i < gffData.size(); i += batchSize) {
			int end = Math.min(i + batchSize, gffData.size());
			List<ImmutablePair<Gff3DTO, Map<String, String>>> batch = gffData.subList(i, end);

			gff3Service.loadExonBatch(batch, entityIdsAdded, locationIdsAdded, associationIdsAdded, dataProvider, assemblyId, history, ph);

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
		List<Long> idsAdded = new ArrayList<Long>();
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		history = bulkLoadFileHistoryDAO.persist(history);
		if (!validateGffAssemblyMatchesSpecies(history, dataProvider, assemblyName)) {
			return new LoadHistoryResponce(history);
		}
		List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedExonGffData = Gff3AttributesHelper.getExonGffData(gffData, dataProvider);
		runLoad(history, null, preProcessedExonGffData, idsAdded, idsAdded, idsAdded, dataProvider, assemblyName);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

}
