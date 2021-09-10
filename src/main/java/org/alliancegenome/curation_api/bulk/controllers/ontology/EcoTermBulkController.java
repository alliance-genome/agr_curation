package org.alliancegenome.curation_api.bulk.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.EcoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class EcoTermBulkController extends BaseOntologyTermBulkController<EcoTermService, ECOTerm, EcoTermDAO> implements EcoTermBulkRESTInterface {

    @Inject EcoTermService ecoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(ecoTermService, ECOTerm.class);
    }

}
