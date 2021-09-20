package org.alliancegenome.curation_api.crud.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.ontology.ZecoTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;

@RequestScoped
public class ZecoTermController extends BaseOntologyTermController<ZecoTermService, ZecoTerm, ZecoTermDAO> implements ZecoTermRESTInterface {

    @Inject ZecoTermService zecoTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(zecoTermService);
    }

}
