package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.DaoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.DaoTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.services.ontology.DaoTermService;

@RequestScoped
public class DaoTermController extends BaseOntologyTermController<DaoTermService, DAOTerm, DaoTermDAO> implements DaoTermRESTInterface {

    @Inject DaoTermService daoTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(daoTermService);
    }

}
