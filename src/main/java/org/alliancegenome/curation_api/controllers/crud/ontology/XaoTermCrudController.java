package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XaoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XaoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XAOTerm;
import org.alliancegenome.curation_api.services.ontology.XaoTermService;

@RequestScoped
public class XaoTermCrudController extends BaseOntologyTermController<XaoTermService, XAOTerm, XaoTermDAO> implements XaoTermCrudInterface {

    @Inject XaoTermService xaoTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(xaoTermService, XAOTerm.class);
    }

}
