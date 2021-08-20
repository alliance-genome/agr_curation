package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.interfaces.bulk.AffectedGenomicModelBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelBulkController implements AffectedGenomicModelBulkRESTInterface {
    
    @Inject
    ConnectionFactory connectionFactory;

    @Override
    public String updateAGMs(AffectedGenomicModelMetaDataDTO agmData) {

        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess("AGM Update", agmData.getData().size());
            for(AffectedGenomicModelDTO allele: agmData.getData()) {
                context.createProducer().send(context.createQueue("agmQueue"), context.createObjectMessage(allele));
                ph.progressProcess();
            }
            ph.finishProcess();
        }

        return "OK";
    }

}
