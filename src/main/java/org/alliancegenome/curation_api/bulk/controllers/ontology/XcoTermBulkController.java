package org.alliancegenome.curation_api.bulk.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.XcoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class XcoTermBulkController extends BaseOntologyTermBulkController<XcoTermService, XcoTerm, XcoTermDAO> implements XcoTermBulkRESTInterface {

    @Inject XcoTermService xcoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(xcoTermService, XcoTerm.class);
    }

}
