package org.alliancegenome.curation_api.jobs;

import java.time.ZonedDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.curationreports.*;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.curationreports.*;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.cronutils.model.*;
import com.cronutils.model.definition.*;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.vertx.mutiny.core.eventbus.EventBus;

@ApplicationScoped
public class ReportScheduler {
	
	@Inject EventBus bus;

	@Inject CurationReportGroupDAO curationReportGroupDAO;
	@Inject CurationReportDAO curationReportDAO;
	
	@ConfigProperty(name = "reports.schedulingEnabled")
	Boolean schedulingEnabled;
	
	private ZonedDateTime lastCheck = null;
	
	@PostConstruct
	public void init() {
		// Set any running jobs to failed as the server has restarted
		SearchResponse<CurationReportGroup> reportGroups = curationReportGroupDAO.findAll(null);
		for(CurationReportGroup g: reportGroups.getResults()) {
			if(g.getCurationReports().size() > 0) {
				for(CurationReport cr: g.getCurationReports()) {
					if(cr.getCurationReportStatus() == null || cr.getCurationReportStatus().isRunning() || cr.getCurationReportStatus().isStarted() || cr.getBirtReportFilePath() != null) {
						cr.setCurationReportStatus(JobStatus.FAILED);
						curationReportDAO.merge(cr);
					}
					if(cr.getCurationReportStatus() == null) {
						cr.setCurationReportStatus(JobStatus.STOPPED);
						curationReportDAO.merge(cr);
					}
					if(cr.getCurationReportStatus().isRunning()) {
						cr.setCurationReportStatus(JobStatus.FAILED);
						curationReportDAO.merge(cr);
					}
				}
			}
		}
	}
	
	@Scheduled(every = "1s")
	public void scheduleGroupJobs() {
		if(schedulingEnabled) {
			ZonedDateTime start = ZonedDateTime.now();
			//Log.info("scheduleGroupJobs: Scheduling Enabled: " + schedulingEnabled);
			SearchResponse<CurationReportGroup> reportGroups = curationReportGroupDAO.findAll(null);
			for(CurationReportGroup g: reportGroups.getResults()) {
				if(g.getCurationReports().size() > 0) {
					for(CurationReport cr: g.getCurationReports()) {
						//Log.info("Report: " + cr);
						if(cr.getScheduleActive() != null && cr.getScheduleActive() && cr.getCronSchedule() != null) {
							CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
							CronParser parser = new CronParser(cronDefinition);
							try {
								Cron unixCron = parser.parse(cr.getCronSchedule());
								unixCron.validate();

								if(lastCheck != null) {
									ExecutionTime executionTime = ExecutionTime.forCron(unixCron);
									ZonedDateTime nextExecution = executionTime.nextExecution(lastCheck).get();

									if(lastCheck.isBefore(nextExecution) && start.isAfter(nextExecution)) {
										Log.info("Need to run Cron: " + cr.getName());
										cr.setSchedulingErrorMessage(null);
										cr.setCurationReportStatus(JobStatus.SCHEDULED_PENDING);
										curationReportDAO.merge(cr);
									}
								}
							} catch (Exception e) {
								cr.setSchedulingErrorMessage(e.getLocalizedMessage());
								cr.setCurationReportStatus(JobStatus.FAILED);
								Log.error(e.getLocalizedMessage());
								curationReportDAO.merge(cr);
							}
						}
					}
				}
			}
			lastCheck = start;
		}
	}
	
	@Scheduled(every = "1s")
	public void runGroupJobs() {
		SearchResponse<CurationReportGroup> reportGroups = curationReportGroupDAO.findAll(null);
		for(CurationReportGroup group: reportGroups.getResults()) {
			for(CurationReport cr: group.getCurationReports()) {
				if(cr.getCurationReportStatus() == null) cr.setCurationReportStatus(JobStatus.FINISHED);
				if(cr.getCurationReportStatus().isPending()) {
					cr.setCurationReportStatus(cr.getCurationReportStatus().getNextStatus());
					curationReportDAO.merge(cr);
					bus.send("RunReport", cr);
				}
			}
		}
	}
	
}
