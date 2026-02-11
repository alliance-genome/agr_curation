package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleGeneAssociationExecutor extends LoadFileExecutor {

	@Inject AlleleGeneAssociationService alleleGeneAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AlleleGeneAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AlleleGeneAssociationDTO> associations = ingestDto.getAlleleGeneAssociationIngestSet();
		if (CollectionUtils.isEmpty(associations)) {
			return;
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(alleleGeneAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		String countType = "Allele Gene Associations";
		bulkLoadFileHistory.setCount(countType, associations.size());
		updateHistory(bulkLoadFileHistory);
		
		boolean success = runLoad(bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded, countType, cleanUp);
		if (success && cleanUp) {
			runCleanup(alleleGeneAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, countType);
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}
	
	protected boolean runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<AlleleGeneAssociationDTO> associationDtos, List<Long> idsAdded, String countType, boolean isFullLoad) {
		if (Thread.currentThread().isInterrupted()) {
			history.setErrorMessage("Thread isInterrupted");
			throw new RuntimeException("Thread isInterrupted");
		}
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		if (CollectionUtils.isNotEmpty(associationDtos)) {
			String loadMessage = "Allele Gene Association update";
			if (dataProvider != null) {
				loadMessage = loadMessage + " for " + dataProvider.name();
			}
			ph.startProcess(loadMessage, associationDtos.size());

			history.setCount(countType, associationDtos.size());
			updateHistory(history);
			Map<Long, Long> isAlleleOfAssociationMap = new HashMap<>();
			for (AlleleGeneAssociationDTO dto : associationDtos) {
				try {
					
					ObjectResponse<AlleleGeneAssociation> dbObject = alleleGeneAssociationService.upsert(dto, dataProvider, isAlleleOfAssociationMap, isFullLoad);
					history.incrementCompleted(countType);
					if (idsAdded != null) {
						idsAdded.add(dbObject.getEntity().getId());
					}
					isAlleleOfAssociationMap.put(dbObject.getEntity().getAlleleAssociationSubject().getId(), dbObject.getEntity().getId());
				} catch (ObjectUpdateException e) {
					history.incrementFailed(countType);
					addException(history, e.getData());
				} catch (KnownIssueValidationException e) {
					Log.debug(e.getMessage());
					history.incrementSkipped(countType);
				} catch (Exception e) {
					e.printStackTrace();
					history.incrementFailed(countType);
					addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
				}
				if (history.getErrorRate(countType) > 0.25) {
					Log.error("Failure Rate > 25% aborting load");
					updateHistory(history);
					updateExceptions(history);
					failLoadAboveErrorRateCutoff(history);
					return false;
				}
				ph.progressProcess();
				if (Thread.currentThread().isInterrupted()) {
					history.setErrorMessage("Thread isInterrupted");
					throw new RuntimeException("Thread isInterrupted");
				}
			}
			updateHistory(history);
			updateExceptions(history);
			ph.finishProcess();
		}
		return true;
	}

}
