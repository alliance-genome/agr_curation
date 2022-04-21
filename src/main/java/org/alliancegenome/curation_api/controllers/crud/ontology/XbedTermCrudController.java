package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XbedTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XbedTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;
import org.alliancegenome.curation_api.services.ontology.XbedTermService;

@RequestScoped
public class XbedTermCrudController extends BaseOntologyTermController<XbedTermService, XBEDTerm, XbedTermDAO> implements XbedTermCrudInterface {

    @Inject XbedTermService xbedTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(xbedTermService, XBEDTerm.class);
    }

}
