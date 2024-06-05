package org.alliancegenome.curation_api.jobs.executors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionIngestFmsDTO;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@ApplicationScoped
public class GeneExpressionExecutor extends LoadFileExecutor {
	@Inject
	GeneExpressionAnnotationService geneExpressionAnnotationService;

	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());

			GeneExpressionIngestFmsDTO geneExpressionIngestFmsDTO = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), GeneExpressionIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(geneExpressionIngestFmsDTO.getData().size());

			AGRCurationSchemaVersion version = GeneExpressionAnnotation.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());
			if (geneExpressionIngestFmsDTO.getMetaData() != null && StringUtils.isNotBlank(geneExpressionIngestFmsDTO.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(geneExpressionIngestFmsDTO.getMetaData().getRelease());
			}

			BulkLoadFileHistory history = new BulkLoadFileHistory(geneExpressionIngestFmsDTO.getData().size());
			createHistory(history, bulkLoadFile);
			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = geneExpressionAnnotationService.getAnnotationIdsByDataProvider(dataProvider);

			runLoad(geneExpressionAnnotationService, history, dataProvider, geneExpressionIngestFmsDTO.getData(), annotationIdsLoaded);

			runCleanup(geneExpressionAnnotationService, history, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, "gene expression annotation", bulkLoadFile.getMd5Sum());
			history.finishLoad();

			finalSaveHistory(history);


		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}
}
