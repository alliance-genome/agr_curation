package org.alliancegenome.curation_api.jobs;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.fms.DataFile;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.fms.DataFileService;
import org.alliancegenome.curation_api.util.FileTransferHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class BulkLoadProcessor {

	@ConfigProperty(name = "bulk.data.loads.s3Bucket")
	String s3Bucket = null;

	@ConfigProperty(name = "bulk.data.loads.s3PathPrefix")
	String s3PathPrefix = null;

	@ConfigProperty(name = "bulk.data.loads.s3AccessKey")
	String s3AccessKey = null;

	@ConfigProperty(name = "bulk.data.loads.s3SecretKey")
	String s3SecretKey = null;

	@Inject
	EventBus bus;
	@Inject
	DataFileService fmsDataFileService;

	@Inject
	BulkLoadDAO bulkLoadDAO;
	@Inject
	BulkManualLoadDAO bulkManualLoadDAO;
	@Inject
	BulkLoadFileDAO bulkLoadFileDAO;
	@Inject
	BulkFMSLoadDAO bulkFMSLoadDAO;
	@Inject
	BulkURLLoadDAO bulkURLLoadDAO;
	@Inject
	BulkLoadJobExecutor bulkLoadJobExecutor;

	protected FileTransferHelper fileHelper = new FileTransferHelper();

	@ConsumeEvent(value = "bulkloadfile", blocking = true) // Triggered by the Scheduler or Forced start
	public void bulkLoadFile(Message<BulkLoadFile> file) {
		BulkLoadFile bulkLoadFile = bulkLoadFileDAO.find(file.body().getId());
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
			bulkLoadJobExecutor.process(bulkLoadFile);
			JobStatus status = bulkLoadFile.getBulkloadStatus().equals(JobStatus.FAILED) ? JobStatus.FAILED : JobStatus.FINISHED;
			endLoadFile(bulkLoadFile, bulkLoadFile.getErrorMessage(), status);

		} catch (Exception e) {
			endLoadFile(bulkLoadFile, "Failed loading: " + bulkLoadFile.getBulkLoad().getName() + " please check the logs for more info. " + bulkLoadFile.getErrorMessage(), JobStatus.FAILED);
			log.error("Load File: " + bulkLoadFile.getBulkLoad().getName() + " is failed");
			e.printStackTrace();
		}

	}

	private String processFMS(String dataType, String dataSubType) {
		List<DataFile> files = fmsDataFileService.getDataFiles(dataType, dataSubType);

		if (files.size() == 1) {
			DataFile df = files.get(0);
			return df.getS3Url();
		} else {
			log.warn("Files: " + files);
			log.warn("Issue pulling files from the FMS: " + dataType + " " + dataSubType);
		}
		return null;
	}

	public void syncWithS3(BulkLoadFile bulkLoadFile) {
		log.info("Syncing with S3");
		log.info("Local: " + bulkLoadFile.getLocalFilePath());
		log.info("S3: " + bulkLoadFile.getS3Path());

		if ((bulkLoadFile.getS3Path() != null || bulkLoadFile.generateS3MD5Path() != null) && bulkLoadFile.getLocalFilePath() == null) {
			File outfile = fileHelper.downloadFileFromS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path());
			if (outfile != null) {
				// log.info(outfile + " is of size: " + outfile.length());
				bulkLoadFile.setFileSize(outfile.length());
				bulkLoadFile.setLocalFilePath(outfile.getAbsolutePath());
			} else {
				// log.error("Failed to download file from S3 Path: " + s3PathPrefix + "/" +
				// bulkLoadFile.generateS3MD5Path());
				bulkLoadFile.setErrorMessage("Failed to download file from S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path());
				bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
			}
			// log.info("Saving File: " + bulkLoadFile);
			bulkLoadFileDAO.merge(bulkLoadFile);
		} else if (bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() != null) {
			if (s3AccessKey != null && s3AccessKey.length() > 0) {
				String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path(), new File(bulkLoadFile.getLocalFilePath()));
				bulkLoadFile.setS3Path(s3Path);
			}
			bulkLoadFileDAO.merge(bulkLoadFile);
		} else if (bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() == null) {
			bulkLoadFile.setErrorMessage("Failed to download or upload file with S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path() + " Local and remote file missing");
			bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
		}
		log.info("Syncing with S3 Finished");
	}

	protected void processFilePath(BulkLoad bulkLoad, String localFilePath) {
		String md5Sum = fileHelper.getMD5SumOfGzipFile(localFilePath);
		log.info("processFilePath: MD5 Sum: " + md5Sum);

		File inputFile = new File(localFilePath);

		BulkLoad load = bulkLoadDAO.find(bulkLoad.getId());

		SearchResponse<BulkLoadFile> bulkLoadFiles = bulkLoadFileDAO.findByField("md5Sum", md5Sum);
		BulkLoadFile bulkLoadFile;

		if (bulkLoadFiles == null || bulkLoadFiles.getTotalResults() == 0) {
			log.info("Bulk File does not exist creating it");
			bulkLoadFile = new BulkLoadFile();
			bulkLoadFile.setBulkLoad(load);
			bulkLoadFile.setMd5Sum(md5Sum);
			bulkLoadFile.setFileSize(inputFile.length());
			if (load.getBulkloadStatus() == JobStatus.FORCED_RUNNING) {
				bulkLoadFile.setBulkloadStatus(JobStatus.FORCED_PENDING);
			}
			if (load.getBulkloadStatus() == JobStatus.SCHEDULED_RUNNING) {
				bulkLoadFile.setBulkloadStatus(JobStatus.SCHEDULED_PENDING);
			}
			if (load.getBulkloadStatus() == JobStatus.MANUAL_RUNNING) {
				bulkLoadFile.setBulkloadStatus(JobStatus.MANUAL_PENDING);
			}

			log.info(load.getBulkloadStatus());

			bulkLoadFile.setLocalFilePath(localFilePath);
			bulkLoadFileDAO.persist(bulkLoadFile);
		} else if (load.getBulkloadStatus().isForced()) {
			bulkLoadFile = bulkLoadFiles.getResults().get(0);
			if (bulkLoadFile.getBulkloadStatus().isNotRunning()) {
				bulkLoadFile.setLocalFilePath(localFilePath);
				bulkLoadFile.setBulkloadStatus(JobStatus.FORCED_PENDING);
			} else {
				log.warn("Bulk File is already running: " + bulkLoadFile.getMd5Sum());
				log.info("Cleaning up downloaded file: " + localFilePath);
				new File(localFilePath).delete();
			}
		} else {
			log.info("Bulk File already exists not creating it");
			bulkLoadFile = bulkLoadFiles.getResults().get(0);
			log.info("Cleaning up downloaded file: " + localFilePath);
			new File(localFilePath).delete();
			bulkLoadFile.setLocalFilePath(null);
		}

		if (!load.getLoadFiles().contains(bulkLoadFile)) {
			load.getLoadFiles().add(bulkLoadFile);
		}
		bulkLoadFileDAO.merge(bulkLoadFile);
		bulkLoadDAO.merge(load);
	}

	protected void startLoad(BulkLoad load) {
		log.info("Load: " + load.getName() + " is starting");

		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		if (!bulkLoad.getBulkloadStatus().isStarted()) {
			log.warn("startLoad: Job is not started returning: " + bulkLoad.getBulkloadStatus());
			return;
		}
		bulkLoad.setBulkloadStatus(bulkLoad.getBulkloadStatus().getNextStatus());
		bulkLoadDAO.merge(bulkLoad);
		log.info("Load: " + bulkLoad.getName() + " is running");
	}

	protected void endLoad(BulkLoad load, String message, JobStatus status) {
		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		bulkLoad.setErrorMessage(message);
		bulkLoad.setBulkloadStatus(status);
		bulkLoadDAO.merge(bulkLoad);
		log.info("Load: " + bulkLoad.getName() + " is finished");
	}

	protected void startLoadFile(BulkLoadFile bulkLoadFile) {
		bulkLoadFile.setBulkloadStatus(bulkLoadFile.getBulkloadStatus().getNextStatus());
		bulkLoadFileDAO.merge(bulkLoadFile);
		log.info("Load File: " + bulkLoadFile.getMd5Sum() + " is running with file: " + bulkLoadFile.getLocalFilePath());
	}

	protected void endLoadFile(BulkLoadFile bulkLoadFile, String message, JobStatus status) {
		if (bulkLoadFile.getLocalFilePath() != null) {
			new File(bulkLoadFile.getLocalFilePath()).delete();
			bulkLoadFile.setLocalFilePath(null);
		}
		bulkLoadFile.setErrorMessage(message);
		bulkLoadFile.setBulkloadStatus(status);
		bulkLoadFile.setDateLastLoaded(OffsetDateTime.now());
		bulkLoadFileDAO.merge(bulkLoadFile);
		log.info("Load File: " + bulkLoadFile.getMd5Sum() + " is finished");
	}

}
