package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.crud.MoleculeRESTInterface;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.services.MoleculeService;

@RequestScoped
public class MoleculeController extends BaseController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeRESTInterface {

    @Inject MoleculeService moleculeService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(moleculeService);
    }

}
