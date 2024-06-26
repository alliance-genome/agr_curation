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
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.Gff3DTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.services.Gff3Service;
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
	
	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {

			CsvSchema psiMiTabSchema = CsvSchemaBuilder.gff3Schema();
			CsvMapper csvMapper = new CsvMapper();
			MappingIterator<Gff3DTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(PsiMiTabDTO.class).with(psiMiTabSchema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));
			List<Gff3DTO> gffData = it.readAll();
			List<String> gffHeaderData = new ArrayList<>();
			for (Gff3DTO gffLine : gffData) {
				if (gffLine.getSeqId().startsWith("#")) {
					gffHeaderData.add(gffLine.getSeqId());
				} else {
					break;
				}
			}
			gffData.subList(0, gffHeaderData.size() - 1).clear();

			
			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());

			Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
			
			// TODO: get ids of previously loaded entities
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(gffData.size());
			createHistory(history, bulkLoadFile);
			runLoad(history, gffHeaderData, gffData, idsAdded, dataProvider);
			
			// TODO: run cleanup
			
			history.finishLoad();
			finalSaveHistory(history);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runLoad(BulkLoadFileHistory history, List<String> gffHeaderData, List<Gff3DTO> gffData,
			Map<String, List<Long>> idsAdded, BackendBulkDataProvider dataProvider) {
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("GFF update for " + dataProvider.name(), (gffData.size() * 2) + 1);
		
		GenomeAssembly assembly = loadGenomeAssembly(history, gffHeaderData, dataProvider, ph);
		loadEntities(history, gffData, idsAdded, dataProvider, ph);
		if (ObjectUtils.isNotEmpty(assembly)) {
			loadAssociations(history, gffData, idsAdded, dataProvider, assembly, ph);
		}
	}

	private GenomeAssembly loadGenomeAssembly(BulkLoadFileHistory history, List<String> gffHeaderData, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		GenomeAssembly assembly = null;
		try {
			assembly = gff3Service.loadGenomeAssembly(gffHeaderData, dataProvider);
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

	private void loadEntities(BulkLoadFileHistory history, List<Gff3DTO> gffData, Map<String, List<Long>> idsAdded,
			BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (Gff3DTO gff3Entry : gffData) {
			try {
				idsAdded = gff3Service.loadEntities(history, gff3Entry, idsAdded, dataProvider);
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
	}

	private void loadAssociations(BulkLoadFileHistory history, List<Gff3DTO> gffData, Map<String, List<Long>> idsAdded,
			BackendBulkDataProvider dataProvider, GenomeAssembly assembly, ProcessDisplayHelper ph) {
		for (Gff3DTO gff3Entry : gffData) {
			try {
				idsAdded = gff3Service.loadAssociations(history, gff3Entry, idsAdded, dataProvider, assembly);
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
			history.incrementCompleted();
		}
	}
}
