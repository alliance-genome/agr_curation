package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.GeneBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneBulkController implements GeneBulkRESTInterface {

    @Inject GeneService geneService;

    @Override
    public String updateBGI(GeneMetaDataDTO geneData) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Gene Update", geneData.getData().size());
        for(GeneDTO gene: geneData.getData()) {
            geneService.processUpdate(gene);

            ph.progressProcess();
        }
        ph.finishProcess();

        return "OK";
    }

}
