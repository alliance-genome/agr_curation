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
import org.alliancegenome.curation_api.services.associations.SequenceTargetingReagentGeneAssociationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SequenceTargetingReagentExecutor extends LoadFileExecutor {
	@Inject
	SequenceTargetingReagentService sqtrService;
	@Inject
	SequenceTargetingReagentGeneAssociationService sqtrGeneAssociationService;

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

			Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
			idsAdded.put("SQTR", new ArrayList<Long>());
			idsAdded.put("SQTRGeneAssociation", new ArrayList<Long>());

			Map<String, List<Long>> previousIds = getPreviouslyLoadedIds(dataProvider);

			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrIngestFmsDTO.getData().size() * 2);

			runLoad(history, dataProvider, sqtrIngestFmsDTO.getData(), idsAdded.get("SQTR"), idsAdded.get("SQTRGeneAssociation"));

			runCleanup(sqtrService, history, dataProvider.name(), previousIds.get("SQTR"), idsAdded.get("SQTR"), "SQTR", bulkLoadFile.getMd5Sum());
			runCleanup(sqtrService, history, dataProvider.name(), previousIds.get("SQTRGeneAssociation"), idsAdded.get("SQTRGeneAssociation"), "SQTR Gene Associations", bulkLoadFile.getMd5Sum());

			history.finishLoad();

			updateHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	private Map<String, List<Long>> getPreviouslyLoadedIds(BackendBulkDataProvider dataProvider) {
		Map<String, List<Long>> previousIds = new HashMap<>();
		
		previousIds.put("SQTR", sqtrService.getIdsByDataProvider(dataProvider.name()));
		previousIds.put("SQTRGeneAssociation", sqtrGeneAssociationService.getIdsByDataProvider(dataProvider.name()));
		
		return previousIds;
	}

	public APIResponse runLoadApi(String dataProviderName, List<SequenceTargetingReagentFmsDTO> sqtrDTOs) {
		List<Long> sqtrIdsLoaded = new ArrayList<>();
		List<Long> sqtrGeneAssociationIdsLoaded = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrDTOs.size() * 2);
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

	private void loadSequenceTargetingReagents(BulkLoadFileHistory history, List<SequenceTargetingReagentFmsDTO> sqtrs,
			List<Long> idsLoaded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
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

	private void loadSequenceTargetingReagentGeneAssociations(BulkLoadFileHistory history,
			List<SequenceTargetingReagentFmsDTO> sqtrs, List<Long> idsLoaded, BackendBulkDataProvider dataProvider,
			ProcessDisplayHelper ph) {

		for (SequenceTargetingReagentFmsDTO dto : sqtrs) {
			try {
				List<Long> associationIds = sqtrGeneAssociationService.loadGeneAssociations(dto, dataProvider);
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
