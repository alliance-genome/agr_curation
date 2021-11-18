package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.consumers.MoleculeDTOConsumer;
import org.alliancegenome.curation_api.interfaces.bulk.MoleculeBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.MoleculeDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.MoleculeMetaDataDTO;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeBulkController implements MoleculeBulkRESTInterface {
    
    @Inject MoleculeDTOConsumer moleculeDTOConsumer; 
    
    @Inject MoleculeService moleculeService;
    
    @Override
    public String updateMolecules(MoleculeMetaDataDTO moleculeData, boolean async) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Molecule Update", moleculeData.getData().size());

        for(MoleculeDTO molecule: moleculeData.getData()) {
            if(async) {
                moleculeDTOConsumer.send(molecule);
            }
            else {
                moleculeService.processUpdate(molecule);
            }
            ph.progressProcess();
        }

        ph.finishProcess();

        return "OK";
    }

}
