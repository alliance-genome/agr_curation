package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.crud.MoleculeCrudInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadJobExecutor;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

@RequestScoped
public class MoleculeCrudController extends BaseCrudController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeCrudInterface {

    @Inject MoleculeService moleculeService;
    
    @Inject BulkLoadJobExecutor bulkLoadJobExecutor;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(moleculeService);
    }

    @Override
    public String updateMolecules(MoleculeMetaDataDTO moleculeData) {
        bulkLoadJobExecutor.processMoleculeDTOData(null, moleculeData);
        return "OK";
    }

}
