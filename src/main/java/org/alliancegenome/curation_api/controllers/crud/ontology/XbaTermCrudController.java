package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XbaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XbaTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XbaTermService;

@RequestScoped
public class XbaTermCrudController extends BaseOntologyTermController<XbaTermService, XBATerm, XbaTermDAO> implements XbaTermCrudInterface {

    @Inject XbaTermService xbaTermService;

    @Override
    @PostConstruct
    public void init() {
        GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
        config.getAltNameSpaces().add("xenopus_anatomy");
        config.getAltNameSpaces().add("xenopus_anatomy_in_vitro");
        setService(xbaTermService, XBATerm.class);
    }

}
