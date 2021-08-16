package org.alliancegenome.curation_api.services;

import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.dto.json.GeneDTO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

import io.quarkus.runtime.*;

@RequestScoped
public class DoTermService extends BaseService<DOTerm, DoTermDAO> implements Runnable {

    @Inject DoTermDAO doTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(doTermDAO);
    }

    @Transactional
    public DOTerm upsert(DOTerm dto) {

        DOTerm term = get(dto.getCurie());

        if(term == null) {
            term = new DOTerm();
            term.setCurie(dto.getCurie());
            term.setName(dto.getName());
            term.setDefinition(dto.getDefinition());
            doTermDAO.persist(term);
        } else {
            term.setName(dto.getName());
            term.setDefinition(dto.getDefinition());
            doTermDAO.merge(term);
        }

        return term;

    }

    @Inject
    ConnectionFactory connectionFactory;
    
    private int threadCount = 5;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
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
            JMSConsumer consumer = context.createConsumer(context.createQueue("doTermQueue"));
            while (true) {
                upsert(consumer.receiveBody(DOTerm.class));
            }
        }

    }
    
    
}
