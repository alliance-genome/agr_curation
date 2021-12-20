package org.alliancegenome.curation_api.controllers.bulk.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.MaTermBulkInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.alliancegenome.curation_api.services.ontology.MaTermService;

@RequestScoped
public class MaTermBulkController extends BaseOntologyTermBulkController<MaTermService, MATerm, MaTermDAO> implements MaTermBulkInterface {

    @Inject MaTermService maTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(maTermService, MATerm.class);
    }

}

