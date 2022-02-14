package org.alliancegenome.curation_api.jobs;

import java.time.ZonedDateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.amazonaws.services.s3.model.BucketAccelerateStatus;
import com.cronutils.model.*;
import com.cronutils.model.definition.*;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.quarkus.scheduler.Scheduled;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
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
                    if(b.getStatus() == BulkLoadStatus.RUNNING) {
                        b.setStatus(BulkLoadStatus.FAILED);
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
                                            log.info("Need to run Cron: " + bsl);
                                            bsl.setSchedulingErrorMessage(null);
                                            bsl.setStatus(BulkLoadStatus.PENDING_START);
                                            bulkLoadDAO.merge(bsl);
                                        }
                                    }
                                } catch (Exception e) {
                                    bsl.setSchedulingErrorMessage(e.getLocalizedMessage());
                                    bsl.setStatus(BulkLoadStatus.FAILED);
                                    log.error(e.getLocalizedMessage());
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
                if(load.getStatus() == BulkLoadStatus.PENDING_START) {
                    load.setStatus(BulkLoadStatus.STARTED);
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
            if(file.getStatus() == BulkLoadStatus.PENDING_START) {
                file.setStatus(BulkLoadStatus.STARTED);
                file.setErrorMessage(null);
                bulkLoadFileDAO.merge(file);
                bus.send("bulkloadfile", file);
            }
            if(file.getStatus() == BulkLoadStatus.FORCED_START) {
                file.setStatus(BulkLoadStatus.FORCED_STARTED);
                file.setErrorMessage(null);
                bulkLoadFileDAO.merge(file);
                bus.send("bulkloadfile", file);
            }
        }
    }
}
