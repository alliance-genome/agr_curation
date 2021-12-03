package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.GoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.GoTermService;

@RequestScoped
public class GoTermBulkController extends BaseOntologyTermBulkController<GoTermService, GOTerm, GoTermDAO> implements GoTermBulkRESTInterface {

    @Inject GoTermService goTermService;

    @Override
    @PostConstruct
    public void init() {
        GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
        config.getAltNameSpaces().add("biological_process");
        config.getAltNameSpaces().add("molecular_function");
        config.getAltNameSpaces().add("cellular_component");
        setService(goTermService, GOTerm.class, config);
    }

}