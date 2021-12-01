package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.DaoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.DaoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;

@RequestScoped
public class DaoTermBulkController extends BaseOntologyTermBulkController<DaoTermService, DAOTerm, DaoTermDAO> implements DaoTermBulkRESTInterface {

    @Inject DaoTermService daoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(daoTermService, DAOTerm.class);
    }

}

