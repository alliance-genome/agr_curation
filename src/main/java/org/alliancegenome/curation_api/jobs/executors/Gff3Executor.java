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
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.CodingSequenceService;
import org.alliancegenome.curation_api.services.ExonService;
import org.alliancegenome.curation_api.services.Gff3Service;
import org.alliancegenome.curation_api.services.TranscriptService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.ObjectUtils;

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

			Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
			idsAdded.put("Transcript", new ArrayList<Long>());
			idsAdded.put("Exon", new ArrayList<Long>());
			idsAdded.put("CodingSequence", new ArrayList<Long>());
			
			Map<String, List<Long>> previousIds = getPreviouslyLoadedIds(dataProvider);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(gffData.size());
			createHistory(history, bulkLoadFile);
			idsAdded = runLoad(history, gffHeaderData, gffData, idsAdded, dataProvider);
			runCleanup(transcriptService, history, dataProvider.name(), previousIds.get("Transcript"), idsAdded.get("Transcript"), "GFF transcript", bulkLoadFile.getMd5Sum());
			runCleanup(exonService, history, dataProvider.name(), previousIds.get("Exon"), idsAdded.get("Exon"), "GFF exon", bulkLoadFile.getMd5Sum());
			runCleanup(cdsService, history, dataProvider.name(), previousIds.get("CodingSequence"), idsAdded.get("CodingSequence"), "GFF coding sequence", bulkLoadFile.getMd5Sum());
			
			history.finishLoad();
			finalSaveHistory(history);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, List<Long>> getPreviouslyLoadedIds(BackendBulkDataProvider dataProvider) {
		Map<String, List<Long>> previousIds = new HashMap<>();
		
		previousIds.put("Transcript", transcriptService.getIdsByDataProvider(dataProvider));
		previousIds.put("Exon", exonService.getIdsByDataProvider(dataProvider));
		previousIds.put("CodingSequence", cdsService.getIdsByDataProvider(dataProvider));
		
		return previousIds;
	}
	
	private Map<String, List<Long>> runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<Gff3DTO> gffData,
			Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider) {
		return runLoad(history, gffHeaderData, gffData, idsAdded, dataProvider, null);
	}

	private Map<String, List<Long>> runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<Gff3DTO> gffData,
			Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider, String assemblyName) {
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF update for " + dataProvider.name(), (gffData.size() * 2) + 1);
		
		GenomeAssembly assembly = loadGenomeAssembly(assemblyName, history, gffHeaderData, dataProvider, ph);
		idsAdded = loadEntities(history, gffData, idsAdded, dataProvider, ph);
		if (ObjectUtils.isNotEmpty(assembly)) {
			idsAdded = loadAssociations(history, gffData, idsAdded, dataProvider, assembly, ph);
		}
		
		return idsAdded;
	}
	
	public APIResponse runLoadApi(String dataProviderName, String assemblyName, List<Gff3DTO> gffData) {
		Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
		idsAdded.put("Transcript", new ArrayList<Long>());
		idsAdded.put("Exon", new ArrayList<Long>());
		idsAdded.put("CodingSequence", new ArrayList<Long>());
		BulkLoadFileHistory history = new BulkLoadFileHistory((gffData.size() * 2) + 1);
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, null, gffData, idsAdded, dataProvider, assemblyName);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	private GenomeAssembly loadGenomeAssembly(String assemblyName, BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		GenomeAssembly assembly = null;
		try {
			assembly = gff3Service.loadGenomeAssembly(assemblyName, gffHeaderData, dataProvider);
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
		return assembly;
	}

	private Map<String, List<Long>> loadEntities(BulkLoadFileHistory history, List<Gff3DTO> gffData, Map<String, List<Long>> idsAdded,
			BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (Gff3DTO gff3Entry : gffData) {
			try {
				idsAdded = gff3Service.loadEntity(history, gff3Entry, idsAdded, dataProvider);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(gff3Entry, e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
		
		return idsAdded;
	}

	private Map<String, List<Long>> loadAssociations(BulkLoadFileHistory history, List<Gff3DTO> gffData, Map<String, List<Long>> idsAdded,
			BackendBulkDataProvider dataProvider, GenomeAssembly assembly, ProcessDisplayHelper ph) {
		for (Gff3DTO gff3Entry : gffData) {
			try {
				idsAdded = gff3Service.loadAssociation(history, gff3Entry, idsAdded, dataProvider, assembly);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(gff3Entry, e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
		
		return idsAdded;
	}
}
