package org.alliancegenome.curation_api.controllers.bulk.ontology;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.ZfaTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.ZfaTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.alliancegenome.curation_api.services.ontology.ZfaTermService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class ZfaTermBulkController extends BaseOntologyTermBulkController<ZfaTermService, ZfaTerm, ZfaTermDAO> implements ZfaTermBulkRESTInterface {

    @Inject
    ZfaTermService ZfaTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(ZfaTermService, ZfaTerm.class);
    }

}

