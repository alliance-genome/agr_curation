package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.CodingSequenceService;
import org.alliancegenome.curation_api.services.ExonService;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.TranscriptService;
import org.alliancegenome.curation_api.services.associations.codingSequenceAssociations.CodingSequenceGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.exonAssociations.ExonGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptCodingSequenceAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptExonAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.transcriptAssociations.TranscriptGenomicLocationAssociationService;
import org.alliancegenome.curation_api.services.helpers.gff3.Gff3AttributesHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Gff3Executor extends LoadFileExecutor {

	@Inject Gff3Service gff3Service;
	@Inject ExonService exonService;
	@Inject CodingSequenceService cdsService;
	@Inject TranscriptService transcriptService;
	@Inject ExonGenomicLocationAssociationService exonLocationService;
	@Inject CodingSequenceGenomicLocationAssociationService cdsLocationService;
	@Inject TranscriptGenomicLocationAssociationService transcriptLocationService;
	@Inject TranscriptGeneAssociationService transcriptGeneService;
	@Inject TranscriptExonAssociationService transcriptExonService;
	@Inject TranscriptCodingSequenceAssociationService transcriptCdsService;
	
	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {

			CsvSchema gff3Schema = CsvSchemaBuilder.gff3Schema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<Gff3DTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(Gff3DTO.class).with(gff3Schema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));
			List<Gff3DTO> gffData = it.readAll();
			List<String> gffHeaderData = new ArrayList<>();
			for (Gff3DTO gffLine : gffData) {
				if (gffLine.getSeqId().startsWith("#")) {
					gffHeaderData.add(gffLine.getSeqId());
				} else {
					break;
				}
			}
			gffData.subList(0, gffHeaderData.size()).clear();
			
			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());

			List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedGffData = preProcessGffData(gffData, dataProvider);
			
			gffData.clear();
			
			Map<String, List<Long>> idsAdded = createIdsAddedMap();
			Map<String, List<Long>> previousIds = getPreviouslyLoadedIds(dataProvider);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory((preProcessedGffData.size() * 3) + 1);
			createHistory(history, bulkLoadFile);
			idsAdded = runLoad(history, gffHeaderData, preProcessedGffData, idsAdded, dataProvider);
			runCleanup(transcriptService, history, dataProvider.name(), previousIds.get("Transcript"), idsAdded.get("Transcript"), "GFF transcript", bulkLoadFile.getMd5Sum());
			runCleanup(exonService, history, dataProvider.name(), previousIds.get("Exon"), idsAdded.get("Exon"), "GFF exon", bulkLoadFile.getMd5Sum());
			runCleanup(cdsService, history, dataProvider.name(), previousIds.get("CodingSequence"), idsAdded.get("CodingSequence"), "GFF coding sequence", bulkLoadFile.getMd5Sum());
			runCleanup(transcriptLocationService, history, dataProvider.name(), previousIds.get("TranscriptGenomicLocationAssociation"), idsAdded.get("TranscriptGenomicLocationAssociation"), "GFF transcript genomic location association", bulkLoadFile.getMd5Sum());
			runCleanup(exonLocationService, history, dataProvider.name(), previousIds.get("ExonGenomicLocationAssociation"), idsAdded.get("ExonGenomicLocationAssociation"), "GFF exon genomic location association", bulkLoadFile.getMd5Sum());
			runCleanup(cdsLocationService, history, dataProvider.name(), previousIds.get("CodingSequenceGenomicLocationAssociation"), idsAdded.get("CodingSequenceGenomicLocationAssociation"), "GFF coding sequence genomic location association", bulkLoadFile.getMd5Sum());
			runCleanup(transcriptGeneService, history, dataProvider.name(), previousIds.get("TranscriptGeneAssociation"), idsAdded.get("TranscriptGeneAssociation"), "GFF transcript gene association", bulkLoadFile.getMd5Sum());
			runCleanup(transcriptExonService, history, dataProvider.name(), previousIds.get("TranscriptExonAssociation"), idsAdded.get("TranscriptExonAssociation"), "GFF transcript exon association", bulkLoadFile.getMd5Sum());
			runCleanup(transcriptCdsService, history, dataProvider.name(), previousIds.get("TranscriptCodingSequenceAssociation"), idsAdded.get("TranscriptCodingSequenceAssociation"), "GFF transcript coding sequence association", bulkLoadFile.getMd5Sum());
			
			history.finishLoad();
			finalSaveHistory(history);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, List<Long>> createIdsAddedMap() {
		Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
		idsAdded.put("Transcript", new ArrayList<Long>());
		idsAdded.put("Exon", new ArrayList<Long>());
		idsAdded.put("CodingSequence", new ArrayList<Long>());
		idsAdded.put("TranscriptGenomicLocationAssociation", new ArrayList<Long>());
		idsAdded.put("ExonGenomicLocationAssociation", new ArrayList<Long>());
		idsAdded.put("CodingSequenceGenomicLocationAssociation", new ArrayList<Long>());
		idsAdded.put("TranscriptGeneAssociation", new ArrayList<Long>());
		idsAdded.put("TranscriptExonAssociation", new ArrayList<Long>());
		idsAdded.put("TranscriptCodingSequenceAssociation", new ArrayList<Long>());
		
		return idsAdded;
	}
	
	private Map<String, List<Long>> getPreviouslyLoadedIds(BackendBulkDataProvider dataProvider) {
		Map<String, List<Long>> previousIds = new HashMap<>();
		
		previousIds.put("Transcript", transcriptService.getIdsByDataProvider(dataProvider));
		previousIds.put("Exon", exonService.getIdsByDataProvider(dataProvider));
		previousIds.put("CodingSequence", cdsService.getIdsByDataProvider(dataProvider));
		previousIds.put("TranscriptGenomicLocationAssociation", transcriptLocationService.getIdsByDataProvider(dataProvider));
		previousIds.put("ExonGenomicLocationAssociation", exonLocationService.getIdsByDataProvider(dataProvider));
		previousIds.put("CodingSequenceGenomicLocationAssociation", cdsLocationService.getIdsByDataProvider(dataProvider));
		previousIds.put("TranscriptGeneAssociation", transcriptGeneService.getIdsByDataProvider(dataProvider));
		previousIds.put("TranscriptExonAssociation", transcriptExonService.getIdsByDataProvider(dataProvider));
		previousIds.put("TranscriptCodingSequenceAssociation", transcriptCdsService.getIdsByDataProvider(dataProvider));
		
		
		return previousIds;
	}
	
	private Map<String, List<Long>> runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData,
			Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider) {
		return runLoad(history, gffHeaderData, gffData, idsAdded, dataProvider, null);
	}

	private Map<String, List<Long>> runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData,
			Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider, String assemblyId) {
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF update for " + dataProvider.name(), (gffData.size() * 3) + 1);
		
		assemblyId = loadGenomeAssembly(assemblyId, history, gffHeaderData, dataProvider, ph);
		idsAdded = loadEntities(history, gffData, idsAdded, dataProvider, ph);
		
		Map<String, String> geneIdCurieMap = gff3Service.getIdCurieMap(gffData, dataProvider);
		
		idsAdded = loadLocationAssociations(history, gffData, idsAdded, dataProvider, assemblyId, geneIdCurieMap, ph);
		idsAdded = loadParentChildAssociations(history, gffData, idsAdded, dataProvider, assemblyId, geneIdCurieMap, ph);

		ph.finishProcess();
		
		return idsAdded;
	}
	
	public APIResponse runLoadApi(String dataProviderName, String assemblyName, List<Gff3DTO> gffData) {
		Map<String, List<Long>> idsAdded = createIdsAddedMap();
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessedGffData = preProcessGffData(gffData, dataProvider);
		BulkLoadFileHistory history = new BulkLoadFileHistory((preProcessedGffData.size() * 3) + 1);
		
		runLoad(history, null, preProcessedGffData, idsAdded, dataProvider, assemblyName);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	private String loadGenomeAssembly(String assemblyName, BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		try {
			assemblyName = gff3Service.loadGenomeAssembly(assemblyName, gffHeaderData, dataProvider);
			history.incrementCompleted();
		} catch (ObjectUpdateException e) {
			history.incrementFailed();
			addException(history, e.getData());
		} catch (Exception e) {
			e.printStackTrace();
			history.incrementFailed();
			addException(history, new ObjectUpdateExceptionData(gffHeaderData, e.getMessage(), e.getStackTrace()));
		}
		updateHistory(history);
		ph.progressProcess();
		
		return assemblyName;
	}

	private Map<String, List<Long>> loadEntities(BulkLoadFileHistory history, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData, Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		int updateThreshhold = 500; // Aim for every 5 seconds (r/s * 5 = this number)
		int updateCounter = 0;
		for (ImmutablePair<Gff3DTO, Map<String, String>> gff3EntryPair : gffData) {
			try {
				idsAdded = gff3Service.loadEntity(history, gff3EntryPair, idsAdded, dataProvider);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
			}
			if (updateCounter++ % updateThreshhold == 0) {
				updateHistory(history);
			}
			ph.progressProcess();
		}
		updateHistory(history);
		return idsAdded;
	}

	private Map<String, List<Long>> loadLocationAssociations(BulkLoadFileHistory history, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData, Map<String, List<Long>> idsAdded,
			BackendBulkDataProvider dataProvider, String assemblyId, Map<String, String> geneIdCurieMap, ProcessDisplayHelper ph) {
		
		for (ImmutablePair<Gff3DTO, Map<String, String>> gff3EntryPair : gffData) {
			try {
				idsAdded = gff3Service.loadLocationAssociations(history, gff3EntryPair, idsAdded, dataProvider, assemblyId, geneIdCurieMap);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
		
		return idsAdded;
	}

	private Map<String, List<Long>> loadParentChildAssociations(BulkLoadFileHistory history, List<ImmutablePair<Gff3DTO, Map<String, String>>> gffData, Map<String, List<Long>> idsAdded,
			BackendBulkDataProvider dataProvider, String assemblyId, Map<String, String> geneIdCurieMap, ProcessDisplayHelper ph) {
		
		for (ImmutablePair<Gff3DTO, Map<String, String>> gff3EntryPair : gffData) {
			try {
				idsAdded = gff3Service.loadParentChildAssociations(history, gff3EntryPair, idsAdded, dataProvider, assemblyId, geneIdCurieMap);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(gff3EntryPair.getKey(), e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
		
		return idsAdded;
	}
	
	private List<ImmutablePair<Gff3DTO, Map<String, String>>> preProcessGffData(List<Gff3DTO> gffData, BackendBulkDataProvider dataProvider) {
		List<ImmutablePair<Gff3DTO, Map<String, String>>> processedGffData = new ArrayList<>();
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF pre-processing for " + dataProvider.name(), gffData.size());
		
		for (Gff3DTO originalGffEntry : gffData) {
			Map<String, String> attributes = Gff3AttributesHelper.getAttributes(originalGffEntry, dataProvider);
			if (attributes.containsKey("Parent") && attributes.get("Parent").indexOf(",") > -1) {
				for (String parent : attributes.get("Parent").split(",")) {
					HashMap<String, String> attributesCopy = new HashMap<>();
					attributesCopy.putAll(attributes);
					String[] parentIdParts = parent.split(":");
					if (parentIdParts.length == 1) {
						parent = dataProvider.name() + ':' + parentIdParts[0];
					}
					attributesCopy.put("Parent", parent);
					processedGffData.add(new ImmutablePair<>(originalGffEntry, attributesCopy));
				}
			} else {
				processedGffData.add(new ImmutablePair<>(originalGffEntry, attributes));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		
		return processedGffData;
	}
}
