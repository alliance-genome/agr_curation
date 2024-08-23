package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
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

			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());
			
			List<Long> datasetIdsLoaded = new ArrayList<>();
			List<Long> htpAnnotaionsIdsLoaded = new ArrayList<>();
			BulkLoadFileHistory history = new BulkLoadFileHistory(htpExpressionDatasetData.getData().size());
			
			runLoad(history, dataProvider, htpExpressionDatasetData.getData(), htpAnnotaionsIdsLoaded, datasetIdsLoaded);
			
			history.finishLoad();
			updateHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}

	private void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDataset, List<Long> htpAnnotaionsIdsLoaded, List<Long> datasetIdsLoaded) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("HTP Expression Dataset Annotation DTO Update for " + dataProvider.name(), htpDataset.size() * 2);

		runLoaddatasetid(externalDataBaseEntityService, history, dataProvider, htpDataset, datasetIdsLoaded, ph);
		runLoadHtpAnnotations(htpExpressionDatasetAnnotationService, history, dataProvider, htpDataset, htpAnnotaionsIdsLoaded, ph);

		ph.finishProcess();
	}

	private void runLoaddatasetid(ExternalDataBaseEntityService externalDataBaseEntityService, BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDatasetData, List<Long> datasetIdsLoaded,  ProcessDisplayHelper ph) {
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
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
		}
		updateHistory(history);
		ph.progressProcess();
	}

	private void runLoadHtpAnnotations(HTPExpressionDatasetAnnotationService htpExpressionDatasetAnnotationService, BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<HTPExpressionDatasetAnnotationFmsDTO> htpDatasetData, List<Long> AnnotaionsIdsLoaded,  ProcessDisplayHelper ph) {
		for (HTPExpressionDatasetAnnotationFmsDTO dto : htpDatasetData) {
			try {
				HTPExpressionDatasetAnnotation dbObject = htpExpressionDatasetAnnotationService.upsert(dto, dataProvider);
				history.incrementCompleted();
				if (AnnotaionsIdsLoaded != null) {
					AnnotaionsIdsLoaded.add(dbObject.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}
		}
		updateHistory(history);
		ph.progressProcess();
	}
}
