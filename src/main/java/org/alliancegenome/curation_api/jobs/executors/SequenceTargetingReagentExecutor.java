package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.SequenceTargetingReagentService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SequenceTargetingReagentExecutor extends LoadFileExecutor {
	@Inject SequenceTargetingReagentService sqtrService;

	public void execLoad(BulkLoadFile bulkLoadFile) {

		try {

			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();

			SequenceTargetingReagentIngestFmsDTO sqtrIngestFmsDTO = mapper.readValue(
					new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())),
					SequenceTargetingReagentIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(sqtrIngestFmsDTO.getData().size());

			AGRCurationSchemaVersion version = SequenceTargetingReagent.class
					.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());

			if (sqtrIngestFmsDTO.getMetaData() != null
					&& StringUtils.isNotBlank(sqtrIngestFmsDTO.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(sqtrIngestFmsDTO.getMetaData().getRelease());
			}

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());

			List<Long> sqtrIdsLoaded = new ArrayList<>();
			List<Long> sqtrIdsBefore = sqtrService.getIdsByDataProvider(dataProvider.name());
			List<Long> sqtrGeneAssociationIdsLoaded = new ArrayList<>();
			List<Long> sqtrGeneAssociationIdsBefore = sqtrService.getIdsByDataProvider(dataProvider.name());


			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrIngestFmsDTO.getData().size());

			runLoad(history, dataProvider, sqtrIngestFmsDTO.getData(), sqtrIdsLoaded, sqtrGeneAssociationIdsLoaded);

			runCleanup(sqtrService, history, dataProvider.name(), sqtrIdsBefore, sqtrIdsLoaded, "SQTR", bulkLoadFile.getMd5Sum());
			runCleanup(sqtrService, history, dataProvider.name(), sqtrGeneAssociationIdsBefore, sqtrGeneAssociationIdsLoaded, "SQTR Gene Associations", bulkLoadFile.getMd5Sum());

			history.finishLoad();

			updateHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	public APIResponse runLoadApi(String dataProviderName, List<SequenceTargetingReagentFmsDTO> sqtrDTOs) {
		List<Long> sqtrIdsLoaded = new ArrayList<>();
		List<Long> sqtrGeneAssociationIdsLoaded = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrDTOs.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, dataProvider, sqtrDTOs, sqtrIdsLoaded, sqtrGeneAssociationIdsLoaded);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

	private void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<SequenceTargetingReagentFmsDTO> sqtrs, List<Long> sqtrIdsLoaded, List<Long> sqtrGeneAssociationIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Sequence Targeting Reagent DTO Update for " + dataProvider.name(), sqtrs.size() * 2);

		loadSequenceTargetingReagents(history, sqtrs, sqtrIdsLoaded, dataProvider, ph);
		loadSequenceTargetingReagentGeneAssociations(history, sqtrs, sqtrGeneAssociationIdsLoaded, dataProvider, ph);

		ph.finishProcess();

	}

	private void loadSequenceTargetingReagents(BulkLoadFileHistory history, List<SequenceTargetingReagentFmsDTO> sqtrs, List<Long> idsLoaded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (SequenceTargetingReagentFmsDTO dto : sqtrs) {
			try {
				SequenceTargetingReagent dbObject = sqtrService.upsert(dto, dataProvider);
				history.incrementCompleted();
				if (idsLoaded != null) {
					idsLoaded.add(dbObject.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
	}

	private void loadSequenceTargetingReagentGeneAssociations(BulkLoadFileHistory history, List<SequenceTargetingReagentFmsDTO> sqtrs, List<Long> idsLoaded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {

		for (SequenceTargetingReagentFmsDTO dto : sqtrs) {
			try {
				List<Long> associationIds = sqtrService.addGeneAssociations(dto, dataProvider);
				history.incrementCompleted();
				if (idsLoaded != null) {
					idsLoaded.addAll(associationIds);
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			updateHistory(history);
			ph.progressProcess();
		}
	}
}
