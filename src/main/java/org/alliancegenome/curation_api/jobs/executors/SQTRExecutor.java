package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.SQTR;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SQTRFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SQTRIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.SQTRService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class SQTRExecutor extends LoadFileExecutor {
	@Inject
	SQTRService sqtrService;

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		try {

			BulkFMSLoad fms = (BulkFMSLoad) bulkLoadFile.getBulkLoad();

			SQTRIngestFmsDTO sqtrData = mapper.readValue(
					new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), SQTRIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(sqtrData.getData().size());

			AGRCurationSchemaVersion version = SQTR.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());
			if (sqtrData.getMetaData() != null && StringUtils.isNotBlank(sqtrData.getMetaData().getRelease()))
				bulkLoadFile.setAllianceMemberReleaseVersion(sqtrData.getMetaData().getRelease());

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());

			List<Long> sqtrIdsLoaded = new ArrayList<>();
			List<Long> sqtrIdsBefore = new ArrayList<>();
			if (cleanUp) {
				sqtrIdsBefore.addAll(sqtrService.getIdsByDataProvider(dataProvider.name()));
				Log.debug("runLoad: Before: total " + sqtrIdsBefore.size());
			}

			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrData.getData().size());

			runLoad(sqtrService, history, dataProvider, sqtrData.getData(), sqtrIdsLoaded);
			
			//TODO: no idea if this is the write overload
			runCleanup(sqtrService, history, bulkLoadFile, sqtrIdsBefore, sqtrIdsLoaded);
			
			history.finishLoad();
			
			updateHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

}
