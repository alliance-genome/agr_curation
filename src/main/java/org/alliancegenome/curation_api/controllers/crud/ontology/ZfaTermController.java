package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ZfaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ZfaTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.alliancegenome.curation_api.services.ontology.ZfaTermService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class ZfaTermController extends BaseOntologyTermController<ZfaTermService, ZfaTerm, ZfaTermDAO> implements ZfaTermRESTInterface {

    @Inject
    ZfaTermService zfaTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(zfaTermService);
    }

}
