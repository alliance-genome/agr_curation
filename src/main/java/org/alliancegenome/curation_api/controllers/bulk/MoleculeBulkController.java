package org.alliancegenome.curation_api.controllers.bulk;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.MoleculeBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.MoleculeDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.MoleculeMetaDataDTO;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeBulkController implements MoleculeBulkRESTInterface {
    
    @Inject MoleculeService moleculeService;
    
    @Override
    public String updateMolecules(MoleculeMetaDataDTO moleculeData) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Molecule Update", moleculeData.getData().size());

        for(MoleculeDTO molecule: moleculeData.getData()) {
           
            moleculeService.processUpdate(molecule);
            
            ph.progressProcess();
        }

        ph.finishProcess();

        return "OK";
    }

}
