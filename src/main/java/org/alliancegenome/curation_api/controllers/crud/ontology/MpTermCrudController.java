package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MpTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.ontology.MpTermService;

@RequestScoped
public class MpTermCrudController extends BaseOntologyTermController<MpTermService, MPTerm, MpTermDAO> implements MpTermCrudInterface {

    @Inject MpTermService mpTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(mpTermService, MPTerm.class);
    }

}
