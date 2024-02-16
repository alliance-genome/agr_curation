package org.alliancegenome.curation_api.jobs.processors;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.BulkLoadCleanUp;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadFileJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class StartLoadProcessor extends BulkLoadProcessor {

	@Inject
	BulkLoadFileDAO bulkLoadFileDAO;

	public void bulkLoadFile(@Observes StartedBulkLoadFileJobEvent event) { // An @Observes method should not be in a super class as then it gets run for every child class
		BulkLoadFile bulkLoadFile = bulkLoadFileDAO.find(event.getId());
		if (!bulkLoadFile.getBulkloadStatus().isStarted()) {
			log.warn("bulkLoadFile: Job is not started returning: " + bulkLoadFile.getBulkloadStatus());
			// endLoad(bulkLoadFile, "Finished ended due to status: " +
			// bulkLoadFile.getBulkloadStatus(), bulkLoadFile.getBulkloadStatus());
			return;
		} else {
			startLoadFile(bulkLoadFile);
		}

		try {
			if (bulkLoadFile.getLocalFilePath() == null || bulkLoadFile.getS3Path() == null) {
				syncWithS3(bulkLoadFile);
			}
			bulkLoadJobExecutor.process(bulkLoadFile, bulkLoadFile.getBulkloadCleanUp() == BulkLoadCleanUp.YES);
			JobStatus status = bulkLoadFile.getBulkloadStatus().equals(JobStatus.FAILED) ? JobStatus.FAILED : JobStatus.FINISHED;
			endLoadFile(bulkLoadFile, bulkLoadFile.getErrorMessage(), status);

		} catch (Exception e) {
			endLoadFile(bulkLoadFile, "Failed loading: " + bulkLoadFile.getBulkLoad().getName() + " please check the logs for more info. " + bulkLoadFile.getErrorMessage(), JobStatus.FAILED);
			log.error("Load File: " + bulkLoadFile.getBulkLoad().getName() + " is failed");
			e.printStackTrace();
		}

	}
}
