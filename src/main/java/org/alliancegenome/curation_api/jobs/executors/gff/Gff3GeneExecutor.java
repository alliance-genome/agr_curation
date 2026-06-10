package org.alliancegenome.curation_api.jobs.executors.gff;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.alliancegenome.curation_api.services.associations.GeneGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.Gff3AttributesHelper;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
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
public class Gff3GeneExecutor extends Gff3Executor {

	@Inject
	GeneGenomicLocationAssociationService geneLocationService;
	@Inject
	Gff3DtoValidator gff3DtoValidator;
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
			ph.startProcess("GFF Gene header pre-processing", gffRawData.size());
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

			List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedGeneGffData = Gff3AttributesHelper.getGeneGffData(gffFileData, species);

			gffFileData.clear();

			List<Long> locationIdsAdded = new ArrayList<>();
			String assemblyId = loadGenomeAssemblyFromGFF(bulkLoadFileHistory, gffHeaderData, species);

			boolean success = runLoad(bulkLoadFileHistory, gffHeaderData, preProcessedGeneGffData, locationIdsAdded, species, assemblyId);
			if (success) {
				runCleanup(geneLocationService, bulkLoadFileHistory, species.getDisplayName(), geneLocationService.getIdsByDataProvider(species), locationIdsAdded, "GFF gene genomic location association");
			}
			bulkLoadFileHistory.finishLoad();
			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
		}
	}

	private boolean runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData, List<Long> locationIdsAdded, Species species, String assemblyId) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF Gene update for " + species.getDisplayName(), gffData.size());

		String countType = "Locations";
		history.setCount(countType, gffData.size());
		updateHistory(history);

		for (ImmutablePair<Gff3DTO, Map<String, String>> gff3EntryPair : gffData) {
			if (assemblyId != null) {
				countType = "Locations";
				try {
					gff3Service.loadGeneLocationAssociations(gff3EntryPair, locationIdsAdded, species, assemblyId);
					history.incrementCompleted(countType);
				} catch (ObjectUpdateException e) {
					history.incrementFailed(countType);
					addException(history, e.getData());
				} catch (KnownIssueValidationException e) {
					Log.debug(e.getMessage());
					history.incrementSkipped(countType);
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed(countType);
					addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
				}
			}
			ph.progressProcess();
		}
		updateHistory(history);
		ph.finishProcess();
		return true;
	}

	public APIResponse runLoadApi(String dataProviderName, String assemblyName, List<Gff3DTO> gffData) {
		List<Long> idsAdded = new ArrayList<>();
		Species species = speciesService.getByDisplayName(dataProviderName);
		List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedGeneGffData = Gff3AttributesHelper.getGeneGffData(gffData, species);
		BulkLoadFileHistory history = new BulkLoadFileHistory();
		history = bulkLoadFileHistoryDAO.persist(history);
		runLoad(history, null, preProcessedGeneGffData, idsAdded, species, assemblyName);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

}
