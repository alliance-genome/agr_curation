package org.alliancegenome.curation_api.controllers.bulk.ontology;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermBulkController;
import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.interfaces.bulk.ontology.CHEBITermBulkInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;

@RequestScoped
public class CHEBITermBulkController extends BaseOntologyTermBulkController<CHEBITermService, CHEBITerm, CHEBITermDAO> implements CHEBITermBulkInterface {
    @Inject
    CHEBITermService CHEBITermService;

    @Override
    @PostConstruct
    public void init() {
        setService(CHEBITermService, CHEBITerm.class);
    }
}