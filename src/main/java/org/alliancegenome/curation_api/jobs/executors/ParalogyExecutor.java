package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.GeneToGeneParalogyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneToGeneParalogy;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ParalogyIngestFmsDTO;
import org.alliancegenome.curation_api.services.GeneToGeneParalogyService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ParalogyExecutor extends LoadFileExecutor {

	@Inject	GeneToGeneParalogyService geneToGeneParalogyService;
	@Inject	GeneToGeneParalogyDAO geneToGeneParalogyDAO;

	public void execLoad(BulkLoadFile bulkLoadFile) {
		try {
			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();

			ParalogyIngestFmsDTO paralogyData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), ParalogyIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(paralogyData.getData().size());

			AGRCurationSchemaVersion version = GeneToGeneParalogy.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());
			if (paralogyData.getMetaData() != null && StringUtils.isNotBlank(paralogyData.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(paralogyData.getMetaData().getRelease());
			}

			List<Long> paralogyIdsLoaded = new ArrayList<>();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());
			List<Long> paralogyPairsBefore = geneToGeneParalogyService.getAllParalogyPairIdsBySubjectGeneDataProvider(dataProvider);
			Log.debug("runLoad: Before: total " + paralogyPairsBefore.size());

			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(paralogyData.getData().size());
			createHistory(history, bulkLoadFile);
			boolean success = runLoad(geneToGeneParalogyService, history, dataProvider, paralogyData.getData(), paralogyIdsLoaded);

			if (success) {
				runCleanup(geneToGeneParalogyService, history, fms.getFmsDataSubType(), paralogyPairsBefore, paralogyIdsLoaded,fms.getFmsDataType(), bulkLoadFile.getMd5Sum(),false);
			}
			history.finishLoad();

			finalSaveHistory(history);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}
}
