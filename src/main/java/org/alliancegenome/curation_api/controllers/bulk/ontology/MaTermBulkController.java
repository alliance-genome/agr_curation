package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.MaTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.services.ontology.*;

@RequestScoped
public class MaTermBulkController extends BaseOntologyTermBulkController<MaTermService, MATerm, MaTermDAO> implements MaTermBulkRESTInterface {

    @Inject MaTermService maTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(maTermService, MATerm.class);
    }

}

