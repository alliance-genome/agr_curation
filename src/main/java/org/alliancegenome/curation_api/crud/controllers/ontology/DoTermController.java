package org.alliancegenome.curation_api.crud.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.DoTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.ontology.DoTermService;

@RequestScoped
public class DoTermController extends BaseOntologyTermController<DoTermService, DOTerm, DoTermDAO> implements DoTermRESTInterface {

    @Inject DoTermService doTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(doTermService);
    }

}
