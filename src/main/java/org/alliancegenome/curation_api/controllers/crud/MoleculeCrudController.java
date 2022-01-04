package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.crud.MoleculeCrudInterface;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

@RequestScoped
public class MoleculeCrudController extends BaseCrudController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeCrudInterface {

    @Inject MoleculeService moleculeService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(moleculeService);
    }

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
