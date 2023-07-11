package org.alliancegenome.curation_api.jobs;

import org.alliancegenome.curation_api.dao.curationreports.CurationReportDAO;
import org.alliancegenome.curation_api.dao.curationreports.CurationReportGroupDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.curationreports.CurationReport;
import org.alliancegenome.curation_api.util.FileTransferHelper;

import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;

public class ReportProcessor {

	@Inject
	EventBus bus;

	@Inject
	CurationReportGroupDAO curationReportGroupDAO;
	@Inject
	CurationReportDAO curationReportDAO;

	@Inject
	ReportJobExecutor reportJobExecutor;

	protected FileTransferHelper fileHelper = new FileTransferHelper();

	@ConsumeEvent(value = "RunReport", blocking = true) // Triggered by the Scheduler or Forced start
	public void curationReport(Message<CurationReport> report) {
		CurationReport curationReport = curationReportDAO.find(report.body().getId());

		if (!curationReport.getCurationReportStatus().isStarted()) {
			Log.warn("curationReport: Job is not started returning: " + curationReport.getCurationReportStatus());
			return;
		} else {
			startReport(curationReport);
		}

		try {
			if (curationReport.getBirtReportFilePath() != null) {
				reportJobExecutor.process(curationReport);
			}
			endReport(curationReport, "", JobStatus.FINISHED);

		} catch (Exception e) {
			endReport(curationReport, "Failed running: " + curationReport.getName() + " please check the logs for more info. " + curationReport.getErrorMessage(), JobStatus.FAILED);
			Log.error("Load File: " + curationReport.getName() + " is failed");
			e.printStackTrace();
		}
	}

	protected void startReport(CurationReport report) {
		Log.info("Report: " + report.getName() + " is starting");

		if (!report.getCurationReportStatus().isStarted()) {
			Log.warn("startReport: Job is not started returning: " + report.getCurationReportStatus());
			return;
		}
		report.setCurationReportStatus(report.getCurationReportStatus().getNextStatus());
		curationReportDAO.merge(report);
		Log.info("Load: " + report.getName() + " is running");
	}

	protected void endReport(CurationReport report, String message, JobStatus status) {
		CurationReport curationReport = curationReportDAO.find(report.getId());
		curationReport.setErrorMessage(message);
		curationReport.setCurationReportStatus(status);
		curationReportDAO.merge(curationReport);
		Log.info("Report: " + curationReport.getName() + " is finished");
	}

}
