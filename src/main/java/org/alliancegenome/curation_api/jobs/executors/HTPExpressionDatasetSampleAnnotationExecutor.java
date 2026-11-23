package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.HTPExpressionDatasetSampleAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetSampleAnnotationIngestFmsDTO;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetSampleAnnotationService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

// SCRUM-6715: loadIngestSet (direct) and execLoad (FMS) both scope their cleanup to the same
// per-provider "before" set (getAnnotationIdsByDataProvider(dataProvider.name())), matching the
// same characteristic already accepted for the sibling HTPExpressionDatasetAnnotationExecutor (and
// VariantExecutor/VariantFmsExecutor before that). A provider should run either HTPDATASAMPLE (FMS)
// or HTP_EXPRESSION_DATASET_SAMPLE_ANNOTATION (direct), never both, for the duration it's on that path.
@ApplicationScoped
public class HTPExpressionDatasetSampleAnnotationExecutor extends LoadFileExecutor {

	private static final String INGEST_SET = "high_throughput_expression_dataset_sample_annotation_ingest_set";

	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject HTPExpressionDatasetSampleAnnotationService htpExpressionDatasetSampleAnnotationService;
	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;

	// SCRUM-6715: direct LinkML DQM submission path, alongside the FMS path below.
	@Override
	protected List<?> getIngestSet(IngestDTO ingestDto) {
		return ingestDto.getHighThroughputExpressionDatasetSampleAnnotationIngestSet();
	}

	@Override
	protected Class<?> getIngestDtoClass() {
		return HTPExpressionDatasetSampleAnnotationDTO.class;
	}

	@Override
	protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {
		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<HTPExpressionDatasetSampleAnnotationDTO> htpSampleAnnotations = ingestDto.getHighThroughputExpressionDatasetSampleAnnotationIngestSet();

		List<Long> htpSampleAnnotationIdsLoaded = new ArrayList<>();
		List<Long> htpSampleAnnotationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			htpSampleAnnotationIdsBefore.addAll(htpExpressionDatasetSampleAnnotationService.getAnnotationIdsByDataProvider(dataProvider.name()));
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(htpSampleAnnotations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(htpExpressionDatasetSampleAnnotationService::upsert, bulkLoadFileHistory, dataProvider, htpSampleAnnotations, htpSampleAnnotationIdsLoaded, countLabel(INGEST_SET));
		if (success && cleanUp) {
			runCleanup(htpExpressionDatasetSampleAnnotationService, bulkLoadFileHistory, dataProvider.name(), htpSampleAnnotationIdsBefore, htpSampleAnnotationIdsLoaded, countLabel(INGEST_SET));
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();

			HTPExpressionDatasetSampleAnnotationIngestFmsDTO htpExpressionDatasetSampleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())), HTPExpressionDatasetSampleAnnotationIngestFmsDTO.class);
			bulkLoadFileHistory.getBulkLoadFile().setRecordCount(htpExpressionDatasetSampleData.getData().size());

			AGRCurationSchemaVersion version = HTPExpressionDatasetSampleAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFileHistory.getBulkLoadFile().setLinkMLSchemaVersion(version.max());
			if (htpExpressionDatasetSampleData.getMetaData() != null && StringUtils.isNotBlank(htpExpressionDatasetSampleData.getMetaData().getRelease())) {
			bulkLoadFileHistory.getBulkLoadFile().setAllianceMemberReleaseVersion(htpExpressionDatasetSampleData.getMetaData().getRelease());
			}

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> htpAnnotationsIdsLoaded = new ArrayList<>();
			List<Long> previousIds = htpExpressionDatasetSampleAnnotationService.getAnnotationIdsByDataProvider(dataProvider.name());
			
			bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

			bulkLoadFileHistory.setCount((long) htpExpressionDatasetSampleData.getData().size());
			updateHistory(bulkLoadFileHistory);
			
			boolean success = runLoad(htpExpressionDatasetSampleAnnotationService, bulkLoadFileHistory, dataProvider, htpExpressionDatasetSampleData.getData(), htpAnnotationsIdsLoaded);
			
			if (success) {
				runCleanup(htpExpressionDatasetSampleAnnotationService, bulkLoadFileHistory, dataProvider.name(), previousIds, htpAnnotationsIdsLoaded, fms.getFmsDataType());
			}
			bulkLoadFileHistory.finishLoad();

			updateHistory(bulkLoadFileHistory);
			updateExceptions(bulkLoadFileHistory);

		} catch (Exception e) {
			failLoad(bulkLoadFileHistory, e);
			e.printStackTrace();
		}
	}
}
