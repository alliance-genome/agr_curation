package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.rest.MoleculeRESTInterface;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.MoleculeService;

@RequestScoped
public class MoleculeController extends BaseController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeRESTInterface {

    @Inject MoleculeService moleculeService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(moleculeService);
    }

    @Override
    public ObjectResponse<Molecule> get(String id) {
        return moleculeService.get(id);
    }

}
