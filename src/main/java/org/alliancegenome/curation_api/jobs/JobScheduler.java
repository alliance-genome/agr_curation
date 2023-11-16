package org.alliancegenome.curation_api.jobs;

import java.io.File;
import java.time.ZonedDateTime;

import org.alliancegenome.curation_api.dao.loads.BulkLoadDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadFileJobEvent;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadFileJobEvent;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkScheduledLoad;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class JobScheduler {

	//@Inject
	//EventBus bus;
	
	@Inject
	Event<PendingBulkLoadJobEvent> pendingJobEvents;
	
	@Inject
	Event<StartedBulkLoadJobEvent> startedJobEvents;
	
	@Inject
	Event<StartedBulkLoadFileJobEvent> startedFileJobEvents;
	
	@Inject
	BulkLoadFileDAO bulkLoadFileDAO;
	@Inject
	BulkLoadGroupDAO groupDAO;
	@Inject
	BulkLoadDAO bulkLoadDAO;

	@ConfigProperty(name = "bulk.data.loads.schedulingEnabled")
	Boolean loadSchedulingEnabled;

	@ConfigProperty(name = "reindex.schedulingEnabled", defaultValue = "false")
	Boolean reindexSchedulingEnabled;

	private ZonedDateTime lastCheck = null;

	@PostConstruct
	public void init() {
		// Set any running jobs to failed as the server has restarted
		//Log.info("Init: ");
		SearchResponse<BulkLoadGroup> groups = groupDAO.findAll();
		for (BulkLoadGroup g : groups.getResults()) {
			if (g.getLoads().size() > 0) {
				for (BulkLoad b : g.getLoads()) {
					for (BulkLoadFile bf : b.getLoadFiles()) {
						if (bf.getBulkloadStatus() == null || bf.getBulkloadStatus().isRunning() || bf.getBulkloadStatus().isStarted() || bf.getLocalFilePath() != null) {
							new File(bf.getLocalFilePath()).delete();
							bf.setLocalFilePath(null);
							bf.setErrorMessage("Failed due to server start up: Process never finished before the server restarted");
							bf.setBulkloadStatus(JobStatus.FAILED);
							bulkLoadFileDAO.merge(bf);
						}
					}
					if (b.getBulkloadStatus() == null) {
						b.setBulkloadStatus(JobStatus.STOPPED);
						bulkLoadDAO.merge(b);
					}
					if (b.getBulkloadStatus().isRunning()) {
						b.setErrorMessage("Failed due to server start up: Process never finished before the server restarted");
						b.setBulkloadStatus(JobStatus.FAILED);
						bulkLoadDAO.merge(b);
					}
				}
			}
		}
	}

	@Scheduled(every = "1s")
	public void scheduleCronGroupJobs() {
		if (loadSchedulingEnabled) {
			ZonedDateTime start = ZonedDateTime.now();
			// log.info("scheduleGroupJobs: Scheduling Enabled: " + loadSchedulingEnabled);
			SearchResponse<BulkLoadGroup> groups = groupDAO.findAll();
			for (BulkLoadGroup g : groups.getResults()) {
				if (g.getLoads().size() > 0) {
					for (BulkLoad b : g.getLoads()) {
						if (b instanceof BulkScheduledLoad bsl) {
							if (bsl.getScheduleActive() != null && bsl.getScheduleActive() && bsl.getCronSchedule() != null) {

								CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
								CronParser parser = new CronParser(cronDefinition);
								try {
									Cron unixCron = parser.parse(bsl.getCronSchedule());
									unixCron.validate();

									if (lastCheck != null) {
										ExecutionTime executionTime = ExecutionTime.forCron(unixCron);
										ZonedDateTime nextExecution = executionTime.nextExecution(lastCheck).get();

										if (lastCheck.isBefore(nextExecution) && start.isAfter(nextExecution)) {
											Log.info("Need to run Cron: " + bsl.getName());
											bsl.setSchedulingErrorMessage(null);
											bsl.setBulkloadStatus(JobStatus.SCHEDULED_PENDING);
											bulkLoadDAO.merge(bsl);
											pendingJobEvents.fire(new PendingBulkLoadJobEvent(bsl.getId()));
										}
									}
								} catch (Exception e) {
									bsl.setSchedulingErrorMessage(e.getLocalizedMessage());
									bsl.setErrorMessage(e.getLocalizedMessage());
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

	public void pendingJobs(@Observes PendingBulkLoadJobEvent event) {
		//Log.info("pendingJobs: " + event.getId());
		BulkLoad load = bulkLoadDAO.find(event.getId());
		if(load != null) {
			if (load.getBulkloadStatus().isPending()) {
				load.setBulkloadStatus(load.getBulkloadStatus().getNextStatus());
				bulkLoadDAO.merge(load);
				//Log.info("Firing Start Event: " + load.getId());
				startedJobEvents.fire(new StartedBulkLoadJobEvent(load.getId()));
			}
		}
	}
	public void pendingFileJobs(@Observes PendingBulkLoadFileJobEvent event) {
		//Log.info("pendingFileJobs: " + event.getId());
		BulkLoadFile fileLoad = bulkLoadFileDAO.find(event.getId());
		if(fileLoad != null) {
			if (fileLoad.getBulkloadStatus().isPending()) {
				fileLoad.setBulkloadStatus(fileLoad.getBulkloadStatus().getNextStatus());
				bulkLoadFileDAO.merge(fileLoad);
				//Log.info("Firing Start Event: " + fileLoad.getId());
				startedFileJobEvents.fire(new StartedBulkLoadFileJobEvent(fileLoad.getId()));
			}
		}
	}
	
	@Scheduled(cron = "0 0 0 ? * SUN")
	public void runMassIndexerEverything() {
		// Not sure what is going to happen when this time's out but should run anyway
		// Defaults taken from the API endpoint
		// DAO used doesn't matter they all have this method

		if(reindexSchedulingEnabled){
			Log.info("Scheduled mass reindexing initiated.");
			bulkLoadDAO.reindexEverything(1000, 10000, 0, 4, 7200, 1);
		}
		else{
			Log.info("Scheduled mass reindexing not initiated (reindex scheduling not enabled).");
		}
	}

}
