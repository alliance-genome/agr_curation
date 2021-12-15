package org.alliancegenome.curation_api.jobs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.response.SearchResponse;

import io.quarkus.scheduler.Scheduled;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadScheduler {

    @Inject
    EventBus bus;

    @Inject
    BulkLoadGroupDAO groupDAO;

    @Transactional
    @Scheduled(every = "1s", identity = "task-job")
    public void scheduleLoadJobs() {
        SearchResponse<BulkLoadGroup> groups = groupDAO.findAll(null);

        for(BulkLoadGroup group: groups.getResults()) {
            for(BulkLoad load: group.getLoads()) {
                //log.info("Load: " + load);
                if(load.getStatus() == BulkLoadStatus.PENDING) {
                    load.setStatus(BulkLoadStatus.STARTED);
                    log.info("Load: " + load + " is starting");
                    bus.send("bulkload", load);
                }
            }
        }
    }

}
