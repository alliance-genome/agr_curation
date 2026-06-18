package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.SequenceTargetingReagentService;
import org.alliancegenome.curation_api.services.SpeciesService;
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
	@Inject
	SpeciesService speciesService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {

		try {

			SequenceTargetingReagentIngestFmsDTO sqtrIngestFmsDTO = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), SequenceTargetingReagentIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(sqtrIngestFmsDTO.getData().size());

			AGRCurationSchemaVersion version = SequenceTargetingReagent.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());

			if (sqtrIngestFmsDTO.getMetaData() != null && StringUtils.isNotBlank(sqtrIngestFmsDTO.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(sqtrIngestFmsDTO.getMetaData().getRelease());
			}

			Species species = bulkLoadFileHistory.getBulkLoad().getSpecies();

			Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
			idsAdded.put("SQTR", new ArrayList<Long>());
			idsAdded.put("SQTRGeneAssociation", new ArrayList<Long>());

			Map<String, List<Long>> previousIds = getPreviouslyLoadedIds(species);

			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount((long) sqtrIngestFmsDTO.getData().size() * 2);
			updateHistory(bulkLoadFileHistory);

			runLoad(bulkLoadFileHistory, species, sqtrIngestFmsDTO.getData(), idsAdded.get("SQTR"), idsAdded.get("SQTRGeneAssociation"));

			runCleanup(sqtrService, bulkLoadFileHistory, species.getDisplayName(), previousIds.get("SQTRGeneAssociation"), idsAdded.get("SQTRGeneAssociation"), "SQTR Gene Associations");
			runCleanup(sqtrService, bulkLoadFileHistory, species.getDisplayName(), previousIds.get("SQTR"), idsAdded.get("SQTR"), "SQTR");

			bulkLoadFileHistory.finishLoad();

			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private Map<String, List<Long>> getPreviouslyLoadedIds(Species species) {
		Map<String, List<Long>> previousIds = new HashMap<>();

		previousIds.put("SQTR", sqtrService.getIdsByDataProvider(species.getDisplayName()));
		previousIds.put("SQTRGeneAssociation", sqtrGeneAssociationService.getIdsBySpecies(species));

		return previousIds;
	}

	public APIResponse runLoadApi(String dataProviderName, List<SequenceTargetingReagentFmsDTO> sqtrDTOs) {
		List<Long> sqtrIdsLoaded = new ArrayList<>();
		List<Long> sqtrGeneAssociationIdsLoaded = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrDTOs.size() * 2);
		history = bulkLoadFileHistoryDAO.persist(history);
		Species species = speciesService.getByDisplayName(dataProviderName);
		runLoad(history, species, sqtrDTOs, sqtrIdsLoaded, sqtrGeneAssociationIdsLoaded);
		history.finishLoad();

		return new LoadHistoryResponce(history);
	}

	private void runLoad(BulkLoadFileHistory history, Species species, List<SequenceTargetingReagentFmsDTO> sqtrs, List<Long> sqtrIdsLoaded, List<Long> sqtrGeneAssociationIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Sequence Targeting Reagent DTO Update for " + species.getDisplayName(), sqtrs.size() * 2);

		loadSequenceTargetingReagents(history, sqtrs, sqtrIdsLoaded, species, ph);
		loadSequenceTargetingReagentGeneAssociations(history, sqtrs, sqtrGeneAssociationIdsLoaded, ph);

		ph.finishProcess();

	}

	private void loadSequenceTargetingReagents(BulkLoadFileHistory history, List<SequenceTargetingReagentFmsDTO> sqtrs,
			List<Long> idsLoaded, Species species, ProcessDisplayHelper ph) {
		for (SequenceTargetingReagentFmsDTO dto : sqtrs) {
			try {
				
				ObjectResponse<SequenceTargetingReagent> dbObject = sqtrService.upsert(dto, species);
				history.incrementCompleted();
				if (idsLoaded != null) {
					idsLoaded.add(dbObject.getEntity().getId());
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
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
	}

	private void loadSequenceTargetingReagentGeneAssociations(BulkLoadFileHistory history,
			List<SequenceTargetingReagentFmsDTO> sqtrs, List<Long> idsLoaded,
			ProcessDisplayHelper ph) {

		for (SequenceTargetingReagentFmsDTO dto : sqtrs) {
			try {
				List<Long> associationIds = sqtrGeneAssociationService.loadGeneAssociations(dto);
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
			if (Thread.currentThread().isInterrupted()) {
				history.setErrorMessage(ApiErrorException.INTERRUPTED_MESSAGE);
				throw new RuntimeException(ApiErrorException.INTERRUPTED_MESSAGE);
			}
		}
	}
}
