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
						if(bf.getStatus() == null || bf.getStatus().isRunning() || bf.getStatus().isStarted() || bf.getLocalFilePath() != null) {
							new File(bf.getLocalFilePath()).delete();
							bf.setLocalFilePath(null);
							bf.setStatus(JobStatus.FAILED);
							bulkLoadFileDAO.merge(bf);
						}
					}
					if(b.getStatus() == null) {
						b.setStatus(JobStatus.STOPPED);
						bulkLoadDAO.merge(b);
					}
					if(b.getStatus().isRunning()) {
						b.setStatus(JobStatus.FAILED);
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
											bsl.setStatus(JobStatus.SCHEDULED_PENDING);
											bulkLoadDAO.merge(bsl);
										}
									}
								} catch (Exception e) {
									bsl.setSchedulingErrorMessage(e.getLocalizedMessage());
									bsl.setStatus(JobStatus.FAILED);
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
				if(load.getStatus() == null) load.setStatus(JobStatus.FINISHED);
				if(load.getStatus().isPending()) {
					load.setStatus(load.getStatus().getNextStatus());
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
			if(file.getStatus() == null) file.setStatus(JobStatus.FINISHED);
			if(file.getStatus().isPending()) {
				file.setStatus(file.getStatus().getNextStatus());
				file.setErrorMessage(null);
				bulkLoadFileDAO.merge(file);
				bus.send("bulkloadfile", file);
			}
		}
	}
}
