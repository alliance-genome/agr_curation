package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.EmapaTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.EmapaTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.EmapaTermService;

@RequestScoped
public class EmapaTermBulkController extends BaseOntologyTermBulkController<EmapaTermService, EMAPATerm, EmapaTermDAO> implements EmapaTermBulkRESTInterface {

    @Inject EmapaTermService emapaTermService;

    @Override
    @PostConstruct
    public void init() {
        GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
        config.getAltNameSpaces().add("anatomical_structure");
        setService(emapaTermService, EMAPATerm.class, config);
    }

}