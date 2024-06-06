package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyGeneratedDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
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
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class OrthologyExecutor extends LoadFileExecutor {

	@Inject GeneToGeneOrthologyGeneratedService generatedOrthologyService;
	@Inject GeneToGeneOrthologyGeneratedDAO generatedOrthologyDAO;

	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();

			OrthologyIngestFmsDTO orthologyData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), OrthologyIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(orthologyData.getData().size());

			AGRCurationSchemaVersion version = GeneToGeneOrthologyGenerated.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());
			if (orthologyData.getMetaData() != null && StringUtils.isNotBlank(orthologyData.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(orthologyData.getMetaData().getRelease());
			}

			List<Long> orthoPairsLoaded = new ArrayList<>();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> orthoPairIdsBefore = generatedOrthologyService.getAllOrthologyPairIdsBySubjectGeneDataProvider(dataProvider);
			log.debug("runLoad: Before: total " + orthoPairIdsBefore.size());

			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(orthologyData.getData().size());
			createHistory(history, bulkLoadFile);
			runLoad(history, fms.getFmsDataSubType(), orthologyData, orthoPairsLoaded);
			runCleanup(history, fms.getFmsDataSubType(), orthoPairIdsBefore, orthoPairsLoaded);
			history.finishLoad();
			finalSaveHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	private void runCleanup(BulkLoadFileHistory history, String dataProvider, List<Long> orthoPairIdsBefore, List<Long> orthoPairIdsAfter) {
		Log.debug("runLoad: After: " + dataProvider + " " + orthoPairIdsAfter.size());

		List<Long> distinctAfter = orthoPairIdsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProvider + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(orthoPairIdsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProvider + " " + idsToRemove.size());

		history.setTotalDeleteRecords((long) idsToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("Deletion/deprecation of orthology pairs " + dataProvider, idsToRemove.size());
		for (Long idToRemove : idsToRemove) {
			try {
				generatedOrthologyDAO.remove(idToRemove);
				history.incrementDeleted();
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
	}

	private void runLoad(BulkLoadFileHistory history, String dataProvider, OrthologyIngestFmsDTO orthologyData, List<Long> orthoPairIdsAdded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess(dataProvider + " Orthology DTO Update", orthologyData.getData().size());

		for (OrthologyFmsDTO orthoPairDTO : orthologyData.getData()) {
			try {
				GeneToGeneOrthologyGenerated orthoPair = generatedOrthologyService.upsert(orthoPairDTO);
				history.incrementCompleted();
				if (orthoPairIdsAdded != null) {
					orthoPairIdsAdded.add(orthoPair.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(orthoPairDTO, e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
		ph.finishProcess();

	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProvider, OrthologyIngestFmsDTO dto) {
		List<Long> orthoPairIdsAdded = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(dto.getData().size());
		runLoad(history, dataProvider, dto, orthoPairIdsAdded);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

}
