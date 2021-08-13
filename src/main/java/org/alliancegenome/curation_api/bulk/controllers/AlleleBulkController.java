package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.interfaces.bulk.AlleleBulkRESTInterface;
import org.alliancegenome.curation_api.model.dto.json.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleBulkController implements AlleleBulkRESTInterface {
    
    @Inject
    ConnectionFactory connectionFactory;

    @Override
    public String updateAlleles(AlleleMetaDataDTO alleleData) {

        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess("Allele Update", alleleData.getData().size());
            for(AlleleDTO allele: alleleData.getData()) {
                context.createProducer().send(context.createQueue("alleleQueue"), context.createObjectMessage(allele));
                ph.progressProcess();
            }
            ph.finishProcess();
        }

        return "OK";
    }

}
