package org.alliancegenome.curation_api.services.loads;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.scheduler.Scheduled;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class LoadScheduler {
    

    
    
    @Scheduled(every = "10s", identity = "task-job")
    public void scheduleLoadJobs() {
        log.info("Scheduled Task");
    }
    

    
    
    
}
