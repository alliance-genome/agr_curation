package org.alliancegenome.curation_api.base;

import java.util.*;
import java.util.concurrent.*;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoader;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseOntologyTermBulkController<S extends BaseOntologyTermService<T, D>, T extends OntologyTerm, D extends BaseDAO<T>> implements Runnable {

    private GenericOntologyLoader<T> loader;

    private BaseOntologyTermService<T, D> service;
    private Class<T> termClazz;

    @Inject ConnectionFactory connectionFactory1;
    @Inject ConnectionFactory connectionFactory2;

    private JMSProducer producer;
    private JMSContext context;

    private int threadCount = 4;
    private String queueName;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadCount);
    
    protected void setService(S service, Class<T> termClazz) {
        this.service = service;
        this.termClazz = termClazz;
        this.queueName = termClazz.getSimpleName() + "Queue";
        loader = new GenericOntologyLoader<T>(termClazz);
    }

    public String updateTerms(String fullText) {

        context = connectionFactory1.createContext(Session.AUTO_ACKNOWLEDGE);
        producer = context.createProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT); // In memory only will loose all messages if the broker restarts

        log.info(context);
        log.info(producer);
        
        try {
            Map<String, T> termMap = loader.load(fullText);

            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess(termClazz.getSimpleName() + " Database Persistance", termMap.size());
            for(String termKey: termMap.keySet()) {
                //service.processUpdate(termMap.get(termKey));
                producer.send(context.createQueue(queueName), context.createObjectMessage(termMap.get(termKey)));

                ph.progressProcess();
            }
            ph.finishProcess();

        } catch (Exception e) {
            e.printStackTrace();
            context.close();
            return "FAIL";
        }
        
        context.close();
        return "OK";
    }

    void onStart(@Observes StartupEvent ev) {
        log.info("BaseOntologyTermService(" + termClazz.getSimpleName() + "): Starting: " + threadCount + " threads Factory: " + connectionFactory1);
        for(int i = 0; i < threadCount; i++) {
            scheduler.scheduleAtFixedRate(new Thread(this), 0L, 10L, TimeUnit.SECONDS); // Only reexecutes the thread if it fails
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("Shutdown");
        scheduler.shutdown();
    }

    @Override @ActivateRequestContext
    public void run() {
        JMSContext ctx = null;
        CircularFifoQueue<Long> queue = new CircularFifoQueue<>(100);
        T ontologyTerm = null;
        try {
            ctx = connectionFactory2.createContext(Session.AUTO_ACKNOWLEDGE);
            JMSConsumer consumer = ctx.createConsumer(ctx.createQueue(queueName));
            Date start;
            Date end;
            int c = 0;
            while (true) {
                ontologyTerm = consumer.receiveBody(termClazz);
                start = new Date();
                service.processUpdate(ontologyTerm);
                end = new Date();
                queue.add(end.getTime() - start.getTime());
                if((c % 200) == 0 && c > 0) {
                    long sum = queue.stream().reduce(0L, Long::sum);
                    long rps = (200 * 1000) / sum;
                    log.info("BaseOntologyTermService(" + termClazz.getSimpleName() + "): " + rps + " r/s");
                }
                c++;
            }
        } catch (Exception e) {
            if(ctx != null) ctx.close();
            log.info("Failed OntologyTerm: " + ontologyTerm);
            log.info("Thread process failed: Error: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
