package org.alliancegenome.curation_api.crud.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.ontology.*;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.alliancegenome.curation_api.services.ontology.MaTermService;

@RequestScoped
public class MaTermController extends BaseOntologyTermController<MaTermService, MATerm, MaTermDAO> implements MaTermRESTInterface {

    @Inject MaTermService maTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(maTermService);
    }

}
