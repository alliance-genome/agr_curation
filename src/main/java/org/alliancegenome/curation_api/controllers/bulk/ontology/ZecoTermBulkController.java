package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.ZecoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ZecoTermBulkController extends BaseOntologyTermBulkController<ZecoTermService, ZecoTerm, ZecoTermDAO> implements ZecoTermBulkRESTInterface {

    @Inject ZecoTermService zecoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(zecoTermService, ZecoTerm.class);
    }

}
