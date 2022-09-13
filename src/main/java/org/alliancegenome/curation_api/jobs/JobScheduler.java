package org.alliancegenome.curation_api.jobs;

import java.io.File;
import java.time.ZonedDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
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
public class JobScheduler {

	@Inject EventBus bus;
	@Inject BulkLoadFileDAO bulkLoadFileDAO;
	@Inject BulkLoadGroupDAO groupDAO;
	@Inject BulkLoadDAO bulkLoadDAO;
	
	@ConfigProperty(name = "bulk.data.loads.schedulingEnabled")
	Boolean schedulingEnabled;
	
	private ZonedDateTime lastCheck = null;
	
	@PostConstruct
	public void init() {
		// Set any running jobs to failed as the server has restarted
		SearchResponse<BulkLoadGroup> groups = groupDAO.findAll(null);
		for(BulkLoadGroup g: groups.getResults()) {
			if(g.getLoads().size() > 0) {
				for(BulkLoad b: g.getLoads()) {
					for(BulkLoadFile bf: b.getLoadFiles()) {
						if(bf.getBulkloadStatus() == null || bf.getBulkloadStatus().isRunning() || bf.getBulkloadStatus().isStarted() || bf.getLocalFilePath() != null) {
							new File(bf.getLocalFilePath()).delete();
							bf.setLocalFilePath(null);
							bf.setBulkloadStatus(JobStatus.FAILED);
							bulkLoadFileDAO.merge(bf);
						}
					}
					if(b.getBulkloadStatus() == null) {
						b.setBulkloadStatus(JobStatus.STOPPED);
						bulkLoadDAO.merge(b);
					}
					if(b.getBulkloadStatus().isRunning()) {
						b.setBulkloadStatus(JobStatus.FAILED);
						bulkLoadDAO.merge(b);
					}
				}
			}
		}
	}

	@Scheduled(every = "1s")
	public void scheduleGroupJobs() {
		if(schedulingEnabled) {
			ZonedDateTime start = ZonedDateTime.now();
			//log.info("scheduleGroupJobs: Scheduling Enabled: " + schedulingEnabled);
			SearchResponse<BulkLoadGroup> groups = groupDAO.findAll(null);
			for(BulkLoadGroup g: groups.getResults()) {
				if(g.getLoads().size() > 0) {
					for(BulkLoad b: g.getLoads()) {
						if(b instanceof BulkScheduledLoad) {
							BulkScheduledLoad bsl = (BulkScheduledLoad)b;
							if(bsl.getScheduleActive() != null && bsl.getScheduleActive() && bsl.getCronSchedule() != null) {
								
								CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
								CronParser parser = new CronParser(cronDefinition);
								try {
									Cron unixCron = parser.parse(bsl.getCronSchedule());
									unixCron.validate();

									if(lastCheck != null) {
										ExecutionTime executionTime = ExecutionTime.forCron(unixCron);
										ZonedDateTime nextExecution = executionTime.nextExecution(lastCheck).get();

										if(lastCheck.isBefore(nextExecution) && start.isAfter(nextExecution)) {
											Log.info("Need to run Cron: " + bsl.getName());
											bsl.setSchedulingErrorMessage(null);
											bsl.setBulkloadStatus(JobStatus.SCHEDULED_PENDING);
											bulkLoadDAO.merge(bsl);
										}
									}
								} catch (Exception e) {
									bsl.setSchedulingErrorMessage(e.getLocalizedMessage());
									bsl.setBulkloadStatus(JobStatus.FAILED);
									Log.error(e.getLocalizedMessage());
									bulkLoadDAO.merge(bsl);
								}
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
		SearchResponse<BulkLoadGroup> groups = groupDAO.findAll(null);
		for(BulkLoadGroup group: groups.getResults()) {
			for(BulkLoad load: group.getLoads()) {
				if(load.getBulkloadStatus() == null) load.setBulkloadStatus(JobStatus.FINISHED);
				if(load.getBulkloadStatus().isPending()) {
					load.setBulkloadStatus(load.getBulkloadStatus().getNextStatus());
					bulkLoadDAO.merge(load);
					bus.send(load.getClass().getSimpleName(), load);
				}
			}
		}
	}

	@Scheduled(every = "1s")
	public void runFileJobs() {
		SearchResponse<BulkLoadFile> res = bulkLoadFileDAO.findAll(null);
		for(BulkLoadFile file: res.getResults()) {
			if(file.getBulkloadStatus() == null) file.setBulkloadStatus(JobStatus.FINISHED);
			if(file.getBulkloadStatus().isPending()) {
				file.setBulkloadStatus(file.getBulkloadStatus().getNextStatus());
				file.setErrorMessage(null);
				bulkLoadFileDAO.merge(file);
				bus.send("bulkloadfile", file);
			}
		}
	}
}
