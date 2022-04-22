package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XpoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XpoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XpoTermService;

@RequestScoped
public class XpoTermCrudController extends BaseOntologyTermController<XpoTermService, XPOTerm, XpoTermDAO> implements XpoTermCrudInterface {

    @Inject XpoTermService xpoTermService;

    @Override
    @PostConstruct
    public void init() {
        GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
        config.setLoadOnlyIRIPrefix("XPO");
        setService(xpoTermService, XPOTerm.class, config);
    }

}
