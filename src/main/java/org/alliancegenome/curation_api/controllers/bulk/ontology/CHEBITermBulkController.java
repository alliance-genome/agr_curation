package org.alliancegenome.curation_api.controllers.bulk.ontology;
import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.CHEBITermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CHEBITermBulkController extends BaseOntologyTermBulkController<CHEBITermService, CHEBITerm, CHEBITermDAO> implements CHEBITermBulkRESTInterface {
    @Inject
    CHEBITermService CHEBITermService;

    @Override
    @PostConstruct
    public void init() {
        setService(CHEBITermService, CHEBITerm.class);
    }
}