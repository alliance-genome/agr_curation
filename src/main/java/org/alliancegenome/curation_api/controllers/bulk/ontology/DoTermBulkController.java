package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.DoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.ontology.DoTermService;

@RequestScoped
public class DoTermBulkController extends BaseOntologyTermBulkController<DoTermService, DOTerm, DoTermDAO> implements DoTermBulkRESTInterface {

    @Inject DoTermService doTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(doTermService, DOTerm.class);
    }

}

