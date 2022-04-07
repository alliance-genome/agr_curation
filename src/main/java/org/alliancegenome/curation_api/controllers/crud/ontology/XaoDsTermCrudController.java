package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XaoDsTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XaoDsTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XAODsTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XaoDsTermService;

@RequestScoped
public class XaoDsTermCrudController extends BaseOntologyTermController<XaoDsTermService, XAODsTerm, XaoDsTermDAO> implements XaoDsTermCrudInterface {

    @Inject XaoDsTermService xaoDsTermService;

    @Override
    @PostConstruct
    public void init() {
        GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
        config.getAltNameSpaces().add("xenopus_developmental_stage");
        setService(xaoDsTermService, XAODsTerm.class, config);
    }

}
