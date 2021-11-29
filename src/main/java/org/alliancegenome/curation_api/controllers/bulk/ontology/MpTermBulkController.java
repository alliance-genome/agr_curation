package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.MpTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.ontology.MpTermService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MpTermBulkController extends BaseOntologyTermBulkController<MpTermService, MPTerm, MpTermDAO> implements MpTermBulkRESTInterface {

    @Inject MpTermService mpTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(mpTermService, MPTerm.class);
    }

}
