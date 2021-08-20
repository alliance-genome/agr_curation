package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends BaseService<AffectedGenomicModel, AffectedGenomicModelDAO> implements Runnable {

    @Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(affectedGenomicModelDAO);
    }

    @Transactional
    public void processUpdate(AffectedGenomicModelDTO agm) {

        AffectedGenomicModel dbAgm = get(agm.getPrimaryID());
        
        if(dbAgm == null) {
            dbAgm = new AffectedGenomicModel();
            dbAgm.setCurie(agm.getPrimaryID());
            dbAgm.setName(agm.getName().substring(0, Math.min(agm.getName().length(), 254)));
            dbAgm.setTaxon(agm.getTaxonId());
            dbAgm.setSubtype(agm.getSubtype());
            affectedGenomicModelDAO.persist(dbAgm);
        } else {
            if(dbAgm.getCurie().equals(agm.getPrimaryID())) {
                dbAgm.setName(agm.getName().substring(0, Math.min(agm.getName().length(), 254)));
                dbAgm.setTaxon(agm.getTaxonId());
                dbAgm.setSubtype(agm.getSubtype());
                affectedGenomicModelDAO.merge(dbAgm);
            }
        }
    }
    
    
    @Inject
    ConnectionFactory connectionFactory;
    
    private int threadCount = 3;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("AffectedGenomicModelService Queue Starting:");
        for(int i = 0; i < threadCount; i++) {
            scheduler.submit(new Thread(this));
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("agmQueue"));
            while (true) {
                processUpdate(consumer.receiveBody(AffectedGenomicModelDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
