package org.alliancegenome.curation_api.consumers;

import java.util.Date;
import java.util.concurrent.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AffectedGenomicModelDTOConsumer implements Runnable {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Inject ConnectionFactory connectionFactory1;
    @Inject ConnectionFactory connectionFactory2;

    private JMSProducer producer;
    private JMSContext context;

    private int threadCount = 4;
    private String queueName = "affectedGenomicModelQueue";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("AffectedGenomicModelDTOConsumer Starting: " + threadCount + " threads Factory: " + connectionFactory1);
        context = connectionFactory1.createContext(Session.AUTO_ACKNOWLEDGE);
        producer = context.createProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT); // In memory only will loose all messages if the broker restarts
        for(int i = 0; i < threadCount; i++) {
            scheduler.scheduleAtFixedRate(new Thread(this), 0L, 60L, TimeUnit.SECONDS); // Only reexecutes the thread if it fails
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
        CircularFifoQueue<Long> queue = new CircularFifoQueue<>(100);
        AffectedGenomicModelDTO agm = null;
        try {
            ctx = connectionFactory2.createContext(Session.AUTO_ACKNOWLEDGE);
            JMSConsumer consumer = ctx.createConsumer(ctx.createQueue(queueName));
            Date start;
            Date end;
            int c = 0;
            while (true) {
                Message message = consumer.receive();
                agm = message.getBody(AffectedGenomicModelDTO.class);
                start = new Date();
                affectedGenomicModelService.processUpdate(agm);
                end = new Date();
                queue.add(end.getTime() - start.getTime());
                if((c % 200) == 0 && c > 0) {
                    long sum = queue.stream().reduce(0L, Long::sum);
                    long rps = (200 * 1000) / sum;
                    log.info("AffectedGenomicModelDTOConsumer " + rps + " r/s");
                }
                c++;
            }
        } catch (Exception e) {
            if(ctx != null) ctx.close();
            log.info("Failed AffectedGenomicModel: " + agm);
            log.info("Thread process failed: Error: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void send(AffectedGenomicModelDTO agm) {
        producer.send(context.createQueue(queueName), context.createObjectMessage(agm));
    }

}
