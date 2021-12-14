package org.alliancegenome.curation_api.services.loads;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.response.SearchResponse;

import io.quarkus.scheduler.Scheduled;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadScheduler {
    
    @Inject
    BulkLoadGroupDAO groupDAO;
    
    @Scheduled(every = "10s", identity = "task-job")
    public void scheduleLoadJobs() {
        SearchResponse<BulkLoadGroup> groups = groupDAO.findAll(null);
        
        for(BulkLoadGroup group: groups.getResults()) {
            log.info(group);
        }
        
    }
    
}
