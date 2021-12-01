package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.WbbtTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBbtTerm;
import org.alliancegenome.curation_api.services.ontology.WbbtTermService;

@RequestScoped
public class WbbtTermBulkController extends BaseOntologyTermBulkController<WbbtTermService, WBbtTerm, WbbtTermDAO> implements WbbtTermBulkRESTInterface {

    @Inject WbbtTermService wbbtTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(wbbtTermService, WBbtTerm.class);
    }

}

