package org.alliancegenome.curation_api.jobs;

import java.io.File;
import java.time.ZonedDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.cronutils.model.*;
import com.cronutils.model.definition.*;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadExecutor {

    @Inject EventBus bus;
    @Inject BulkLoadGroupDAO groupDAO;
    @Inject BulkLoadFileDAO bulkLoadFileDAO;
    @Inject BulkLoadDAO bulkLoadDAO;
    @Inject BulkLoadFileProcessor bulkLoadFileProcessor;
    @Inject BulkLoadProcessor bulkLoadProcessor;
    
    @ConfigProperty(name = "bulk.data.loads.schedulingEnabled")
    Boolean schedulingEnabled;
    
    private ZonedDateTime lastCheck = null;

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
                            if(bsl.getScheduleActive() && bsl.getCronSchedule() != null) {
                                
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
                                            bsl.setStatus(BulkLoadStatus.PENDING);
                                            bulkLoadDAO.merge(bsl);
                                        }
                                    }
                                } catch (Exception e) {
                                    bsl.setSchedulingErrorMessage(e.getLocalizedMessage());
                                    bsl.setStatus(BulkLoadStatus.FAILED);
                                    //log.error(e.getLocalizedMessage());
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
                if(load.getStatus() == BulkLoadStatus.PENDING) {
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
            if(file.getStatus() == BulkLoadStatus.PENDING) {
                file.setStatus(BulkLoadStatus.STARTED);
                file.setErrorMessage(null);
                bulkLoadFileDAO.merge(file);
                bus.send("bulkloadfile", file);
            }
        }
    }

    @ConsumeEvent(value = "BulkURLLoad", blocking = true)
    public void processBulkURLLoad(Message<BulkURLLoad> load) {
        startLoad(load.body());
        bulkLoadFileProcessor.process(load.body());
    }

    @ConsumeEvent(value = "BulkFMSLoad", blocking = true)
    public void processBulkFMSLoad(Message<BulkFMSLoad> load) {
        startLoad(load.body());
        bulkLoadFileProcessor.process(load.body());
    }

    @ConsumeEvent(value = "BulkManualLoad", blocking = true)
    public void processBulkManualLoad(Message<BulkManualLoad> load) {
        startLoad(load.body());
        bulkLoadFileProcessor.process(load.body());
    }

    @ConsumeEvent(value = "bulkloadfile", blocking = true)
    public void bulkLoadFile(Message<BulkLoadFile> file) {
        BulkLoadFile bulkLoadFile = bulkLoadFileDAO.find(file.body().getId());
        if(bulkLoadFile.getStatus() != BulkLoadStatus.STARTED) {
            log.warn("bulkLoadFile: Job is not started returning: " + bulkLoadFile.getStatus());
            endProcessing(bulkLoadFile, bulkLoadFile.getStatus(), "Finished ended due to status: " + bulkLoadFile.getStatus());
            return;
        }

        bulkLoadFile.setStatus(BulkLoadStatus.RUNNING);
        bulkLoadFileDAO.merge(bulkLoadFile);

        log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " is running with file: " + bulkLoadFile.getLocalFilePath());

        try {
            if(file.body().getLocalFilePath() == null || file.body().getS3Path() == null) {
                bulkLoadFileProcessor.syncWithS3(bulkLoadFile);
            }
            bulkLoadProcessor.process(bulkLoadFile);
            endProcessing(bulkLoadFile, BulkLoadStatus.FINISHED, "");
            log.info("Load: " + bulkLoadFile + " is finished");
            
        } catch (Exception e) {
            endProcessing(bulkLoadFile, BulkLoadStatus.FAILED, "Failed loading: " + file.body().getBulkLoad().getName() + " please check the logs for more info. " + bulkLoadFile.getErrorMessage());
            log.error("Load: " + bulkLoadFile.getBulkLoad().getName() + " is failed");
            e.printStackTrace();
        }
        
    }
    

    private void startLoad(BulkLoad load) {
        log.info("Load: " + load.getName() + " is starting");

        BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
        if(bulkLoad.getStatus() != BulkLoadStatus.STARTED) {
            log.warn("startLoad: Job is not started returning: " + bulkLoad.getStatus());
            return;
        }

        bulkLoad.setStatus(BulkLoadStatus.RUNNING);
        bulkLoadDAO.merge(bulkLoad);
        log.info("Load: " + bulkLoad.getName() + " is running");
    }
    
    private void endProcessing(BulkLoadFile bulkLoadFile, BulkLoadStatus status, String message) {
        bulkLoadFile.getBulkLoad().setStatus(status);
        bulkLoadDAO.merge(bulkLoadFile.getBulkLoad());
        
        if(bulkLoadFile.getLocalFilePath() != null) {
            new File(bulkLoadFile.getLocalFilePath()).delete();
        }
        
        bulkLoadFile.setErrorMessage(message);
        bulkLoadFile.setStatus(status);
        bulkLoadFile.setLocalFilePath(null);
        bulkLoadFileDAO.merge(bulkLoadFile);
    }

}
