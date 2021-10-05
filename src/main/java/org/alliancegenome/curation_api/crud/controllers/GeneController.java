package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.interfaces.rest.GeneRESTInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneService;

@RequestScoped
public class GeneController extends BaseController<GeneService, Gene, GeneDAO> implements GeneRESTInterface {

    @Inject GeneService geneService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(geneService);
    }

    @Override
    public ObjectResponse<Gene> get(String id) {
        return geneService.get(id);
    }

}
