package org.alliancegenome.curation_api.crud.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.ontology.XcoTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;
import org.alliancegenome.curation_api.services.ontology.XcoTermService;

@RequestScoped
public class XcoTermController extends BaseOntologyTermController<XcoTermService, XcoTerm, XcoTermDAO> implements XcoTermRESTInterface {

    @Inject XcoTermService xcoTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(xcoTermService);
    }

}
