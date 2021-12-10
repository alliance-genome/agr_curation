package org.alliancegenome.curation_api.controllers.bulk.ontology;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Session;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.EcoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.services.helpers.EcoTermLoadHelper;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class EcoTermBulkController extends BaseOntologyTermBulkController<EcoTermService, EcoTerm, EcoTermDAO> implements EcoTermBulkRESTInterface {
    
    @Inject EcoTermService ecoTermService;
    @Inject EcoTermDAO ecoTermDAO;
    @Inject ConnectionFactory connectionFactory;
    

    @Override
    @PostConstruct
    public void init() {
        setService(ecoTermService, EcoTerm.class);
    }

    @Override
    public String updateTerms(String fullText) {

        context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
        producer = context.createProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT); // In memory only will loose all messages if the broker restarts

        log.info(context);
        log.info(producer);
        
        EcoTermLoadHelper ecoTermHelper = new EcoTermLoadHelper();
        
        try {
            Map<String, EcoTerm> termMap = loader.load(fullText);
            termMap = ecoTermHelper.addAbbreviations(termMap);      
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
}