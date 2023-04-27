package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.OrthologyIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class OrthologyExecutor extends LoadFileExecutor {

	@Inject
	GeneToGeneOrthologyGeneratedService generatedOrthologyService;

	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			
			OrthologyIngestFmsDTO orthologyData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), OrthologyIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(orthologyData.getData().size());
			
			AGRCurationSchemaVersion version = GeneToGeneOrthologyGenerated.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());
			
			List<Pair<String, String>> orthoPairsLoaded = new ArrayList<>();
			String dataProviderAbbreviation = fms.getFmsDataSubType().equals("HUMAN") ? "OMIM" : fms.getFmsDataSubType();
			List<Object[]> orthoPairsBefore = generatedOrthologyService.getAllOrthologyPairsBySubjectGeneDataProvider(dataProviderAbbreviation);
			log.debug("runLoad: Before: total " + orthoPairsBefore.size());
			
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(orthologyData.getData().size());
			
			runLoad(history, fms.getFmsDataSubType(), orthologyData, orthoPairsLoaded);
			
			runCleanup(history, fms.getFmsDataSubType(), orthoPairsBefore, orthoPairsLoaded);

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}


	private void runCleanup(BulkLoadFileHistory history, String dataProvider, List<Object[]> orthoPairsBefore, List<Pair<String, String>> orthoPairsAfter) {
		Log.debug("runLoad: After: " + dataProvider + " " + orthoPairsAfter.size());
		
		List<Pair<String, String>> transformedPairsBefore = new ArrayList<>();
		for (Object[] pair : orthoPairsBefore) {
			transformedPairsBefore.add(Pair.of((String) pair[0], (String) pair[1]));
		}
		List<Pair<String, String>> distinctAfter = orthoPairsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProvider + " " + distinctAfter.size());
		
		List<Pair<String, String>> pairsToRemove = ListUtils.subtract(transformedPairsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProvider + " " + pairsToRemove.size());

		history.setTotalDeleteRecords((long)pairsToRemove.size());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of orthology pairs " + dataProvider, pairsToRemove.size());
		for (Pair<String,String> pairToRemove : pairsToRemove) {
			try {
				generatedOrthologyService.removeNonUpdated(pairToRemove);
				history.incrementDeleted();
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
	}


	public void runLoad(BulkLoadFileHistory history, String dataProvider, OrthologyIngestFmsDTO orthologyData, List<Pair<String, String>> orthoPairsAdded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess(dataProvider + " Orthology DTO Update", orthologyData.getData().size());

		for (OrthologyFmsDTO orthoPairDTO : orthologyData.getData()) {
			try {
				GeneToGeneOrthologyGenerated orthoPair = generatedOrthologyService.upsert(orthoPairDTO);
				history.incrementCompleted();
				if (orthoPairsAdded != null)
					orthoPairsAdded.add(Pair.of(orthoPair.getSubjectGene().getCurie(), orthoPair.getObjectGene().getCurie()));
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(orthoPairDTO, e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();

	}
	
	// Gets called from the API directly
	public APIResponse runLoad(String dataProvider, OrthologyIngestFmsDTO dto) {
		List<Pair<String, String>> orthoPairsAdded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(dto.getData().size());
		runLoad(history, dataProvider, dto, orthoPairsAdded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

}
