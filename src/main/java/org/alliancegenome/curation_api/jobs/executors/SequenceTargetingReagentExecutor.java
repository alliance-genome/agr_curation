package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentIngestFmsDTO;
import org.alliancegenome.curation_api.services.SequenceTargetingReagentService;
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
					new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), SequenceTargetingReagentIngestFmsDTO.class);
			bulkLoadFile.setRecordCount(sqtrIngestFmsDTO.getData().size());

			AGRCurationSchemaVersion version = SequenceTargetingReagent.class.getAnnotation(AGRCurationSchemaVersion.class);
			bulkLoadFile.setLinkMLSchemaVersion(version.max());

			if (sqtrIngestFmsDTO.getMetaData() != null && StringUtils.isNotBlank(sqtrIngestFmsDTO.getMetaData().getRelease())) {
				bulkLoadFile.setAllianceMemberReleaseVersion(sqtrIngestFmsDTO.getMetaData().getRelease());
			} 

			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fms.getFmsDataSubType());

			List<Long> sqtrIdsLoaded = new ArrayList<>();

			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(sqtrIngestFmsDTO.getData().size());

			runLoad(sqtrService, history, dataProvider, sqtrIngestFmsDTO.getData(), sqtrIdsLoaded);
			
			history.finishLoad();
			
			updateHistory(history);
		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

}
