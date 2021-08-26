package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.interfaces.bulk.AffectedGenomicModelBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelBulkController implements AffectedGenomicModelBulkRESTInterface {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Override
    public String updateAGMs(AffectedGenomicModelMetaDataDTO agmData) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("AGM Update", agmData.getData().size());
        for(AffectedGenomicModelDTO agm: agmData.getData()) {
            affectedGenomicModelService.processUpdate(agm);
            ph.progressProcess();
        }
        ph.finishProcess();

        return "OK";
    }

}
