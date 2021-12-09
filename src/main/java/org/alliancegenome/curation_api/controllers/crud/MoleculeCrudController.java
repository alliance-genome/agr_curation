package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.crud.MoleculeCrudInterface;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.services.MoleculeService;

@RequestScoped
public class MoleculeCrudController extends BaseCrudController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeCrudInterface {

    @Inject MoleculeService moleculeService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(moleculeService);
    }

}
