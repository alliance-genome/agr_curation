package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.interfaces.bulk.AlleleBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleBulkController implements AlleleBulkRESTInterface {

    @Inject AlleleService alleleService;

    @Override
    public String updateAlleles(AlleleMetaDataDTO alleleData) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Allele Update", alleleData.getData().size());
        for(AlleleDTO allele: alleleData.getData()) {
            alleleService.processUpdate(allele);
            ph.progressProcess();
        }
        ph.finishProcess();


        return "OK";
    }

}
