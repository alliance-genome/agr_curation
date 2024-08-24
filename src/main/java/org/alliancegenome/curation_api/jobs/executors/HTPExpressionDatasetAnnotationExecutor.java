package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HTPExpressionDatasetAnnotationExecutor extends LoadFileExecutor {

	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject HTPExpressionDatasetAnnotationService htpExpressionDatasetAnnotationService;
	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	
	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

			HTPExpressionDatasetAnnotationIngestFmsDTO htpExpressionDatasetData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), HTPExpressionDatasetAnnotationIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(htpExpressionDatasetData.getData().size());

			AGRCurationSchemaVersion version = HTPExpressionDatasetAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (htpExpressionDatasetData.getMetaData() != null && StringUtils.isNotBlank(htpExpressionDatasetData.getMetaData().getRelease())) {
				bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(htpExpressionDatasetData.getMetaData().getRelease());
			}

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());

			bulkLoadFileDAO.merge(bulkLoadFile);

			Map<String, List<Long>> idsAdded = new HashMap<String, List<Long>>();
			idsAdded.put("HTPDatasetIds", new ArrayList<Long>());
			idsAdded.put("HTPDatasetAnnotationIds", new ArrayList<Long>());

			Map<String, List<Long>> previousIds = getPreviouslyLoadedIds(dataProvider);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(htpExpressionDatasetData.getData().size());
			
			runLoad(history, dataProvider, htpExpressionDatasetData.getData(), idsAdded.get("HTPDatasetIds"), idsAdded.get("HTPDatasetAnnotationIds"));
			
			runCleanup(externalDataBaseEntityService, history, dataProvider.name(), previousIds.get("HTPDatasetIds"), idsAdded.get("HTPDatasetIds"), "ExternalDatabaseEntities", bulkLoadFile.getMd5Sum());
			runCleanup(htpExpressionDatasetAnnotationService, history, dataProvider.name(), previousIds.get("HTPDatasetAnnotationIds"), idsAdded.get("HTPDatasetAnnotationIds"), fms.getFmsDataType(), bulkLoadFile.getMd5Sum());

			history.finishLoad();
			updateHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDataset, List<Long> datasetIdsLoaded, List<Long> htpAnnotationsIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("HTP Expression Dataset Annotation DTO Update for " + dataProvider.name(), htpDataset.size() * 2);

		loadHtpDatasets(externalDataBaseEntityService, history, dataProvider, htpDataset, datasetIdsLoaded, ph);
		loadHtpDatasetAnnotations(htpExpressionDatasetAnnotationService, history, dataProvider, htpDataset, htpAnnotationsIdsLoaded, ph);

		ph.finishProcess();
	}

	private void loadHtpDatasets(ExternalDataBaseEntityService externalDataBaseEntityService, BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDatasetData, List<Long> datasetIdsLoaded, ProcessDisplayHelper ph) {
		for (HTPExpressionDatasetAnnotationFmsDTO dto : htpDatasetData) {
			try {
				ExternalDataBaseEntity dbObject = externalDataBaseEntityService.upsert(dto.getDatasetId(), dataProvider);
				history.incrementCompleted();
				if (datasetIdsLoaded != null) {
					datasetIdsLoaded.add(dbObject.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			if (history.getErrorRate() > 0.25) {
				Log.error("Failure Rate > 25% aborting load");
				finalSaveHistory(history);
				failLoadAboveErrorRateCutoff(history.getBulkLoadFile());
			}
		}
		updateHistory(history);
		ph.progressProcess();
	}

	private void loadHtpDatasetAnnotations(HTPExpressionDatasetAnnotationService htpExpressionDatasetAnnotationService, BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDatasetData, List<Long> annotationsIdsLoaded, ProcessDisplayHelper ph) {
		for (HTPExpressionDatasetAnnotationFmsDTO dto : htpDatasetData) {
			try {
				HTPExpressionDatasetAnnotation dbObject = htpExpressionDatasetAnnotationService.upsert(dto, dataProvider);
				history.incrementCompleted();
				if (annotationsIdsLoaded != null) {
					annotationsIdsLoaded.add(dbObject.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
			if (history.getErrorRate() > 0.25) {
				Log.error("Failure Rate > 25% aborting load");
				finalSaveHistory(history);
				failLoadAboveErrorRateCutoff(history.getBulkLoadFile());
			}
		}
		updateHistory(history);
		ph.progressProcess();
	}

	private Map<String, List<Long>> getPreviouslyLoadedIds(BackendBulkDataProvider dataProvider) {
		Map<String, List<Long>> previousIds = new HashMap<>();
		
		previousIds.put("HTPDatasetIds", externalDataBaseEntityService.getDatasetIdsByDataProvider(dataProvider.name()));
		previousIds.put("HTPDatasetAnnotationIds", htpExpressionDatasetAnnotationService.getAnnotationIdsByDataProvider(dataProvider.name()));
		
		return previousIds;
	}
}
