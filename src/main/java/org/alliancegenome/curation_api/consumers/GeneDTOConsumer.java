package org.alliancegenome.curation_api.consumers;

import java.util.Date;
import java.util.concurrent.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.services.GeneService;
import org.apache.commons.collections4.queue.CircularFifoQueue;

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
    
    private String queueName = "geneQueue";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("GeneDTOConsumer Starting:" + threadCount + " Factory: " + connectionFactory1);
        context = connectionFactory1.createContext(Session.AUTO_ACKNOWLEDGE);
        producer = context.createProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT); // In memory only will loose all messages if the broker restarts
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
        CircularFifoQueue<Long> queue = new CircularFifoQueue<>(100);
        GeneDTO gene = null;
        try {
            ctx = connectionFactory2.createContext(Session.AUTO_ACKNOWLEDGE);
            JMSConsumer consumer = ctx.createConsumer(ctx.createQueue(queueName));
            Date start;
            Date end;
            int c = 0;
            while (true) {
                gene = consumer.receiveBody(GeneDTO.class);
                start = new Date();
                geneService.processUpdate(gene);
                end = new Date();
                queue.add(end.getTime() - start.getTime());
                if((c % 200) == 0 && c > 0) {
                    long sum = queue.stream().reduce(0L, Long::sum);
                    long rps = (200 * 1000) / sum;
                    log.info("GeneDTOConsumer " + rps + " r/s");
                }
                c++;
            }
        } catch (Exception e) {
            if(ctx != null) ctx.close();
            log.info("Gene: " + gene);
            log.info("Thread process failed: Error: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void send(GeneDTO gene) {
        producer.send(context.createQueue(queueName), context.createObjectMessage(gene));
    }

}
