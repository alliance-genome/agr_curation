package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.FbdvTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.FbdvTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.FBdvTerm;
import org.alliancegenome.curation_api.services.ontology.FbdvTermService;

@RequestScoped
public class FbdvTermCrudController extends BaseOntologyTermController<FbdvTermService, FBdvTerm, FbdvTermDAO> implements FbdvTermCrudInterface {

    @Inject FbdvTermService fbdvTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(fbdvTermService, FBdvTerm.class);
    }

}
