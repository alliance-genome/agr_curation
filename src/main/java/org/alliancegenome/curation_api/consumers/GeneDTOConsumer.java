package org.alliancegenome.curation_api.consumers;

import java.util.concurrent.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.services.GeneService;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneDTOConsumer implements Runnable {

    @Inject GeneService geneService;

    @Inject ConnectionFactory connectionFactory1;
    @Inject ConnectionFactory connectionFactory2;
    
    private JMSProducer producer;
    private JMSContext context;

    private int threadCount = 4;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("GeneDTOConsumer Starting:" + threadCount);
        context = connectionFactory1.createContext(Session.AUTO_ACKNOWLEDGE);
        producer = context.createProducer();
        for(int i = 0; i < threadCount; i++) {
            scheduler.scheduleWithFixedDelay(new Thread(this), 0L, 5L, TimeUnit.SECONDS);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("Shutdown");
        scheduler.shutdown();
        context.close();
    }

    @Override @ActivateRequestContext
    public void run() {
        JMSContext ctx = null;
        try {
            ctx = connectionFactory2.createContext(Session.AUTO_ACKNOWLEDGE);
            JMSConsumer consumer = ctx.createConsumer(ctx.createQueue("geneQueue"));
            while (true) {
                geneService.processUpdate(consumer.receiveBody(GeneDTO.class));
            }
        } catch (Exception e) {
            if(ctx != null) ctx.close();
            log.info("Thread process failed: Error: " + e);
            throw new RuntimeException(e);
        }
    }

    public void send(GeneDTO gene) {
        producer.send(context.createQueue("geneQueue"), context.createObjectMessage(gene));
    }

}
