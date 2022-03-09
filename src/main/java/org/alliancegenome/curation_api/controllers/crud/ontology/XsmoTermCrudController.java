package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XsmoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XsmoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XsmoTermService;

@RequestScoped
public class XsmoTermCrudController extends BaseOntologyTermController<XsmoTermService, XSMOTerm, XsmoTermDAO> implements XsmoTermCrudInterface {

    @Inject XsmoTermService xsmoTermService;

    @Override
    @PostConstruct
    public void init() {
        GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
        config.setIgnoreEntitiesWithChebiXref(true);
        setService(xsmoTermService, XSMOTerm.class, config);
    }

}
