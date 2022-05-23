package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneExecutor;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneService;

@RequestScoped
public class GeneCrudController extends BaseCrudController<GeneService, Gene, GeneDAO> implements GeneCrudInterface {

    @Inject GeneService geneService;
    
    @Inject GeneExecutor geneExecutor;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(geneService);
    }

    @Override
    public ObjectResponse<Gene> get(String id) {
        return geneService.get(id);
    }
    
    @Override
    public APIResponse updateGenes(List<GeneDTO> geneData) {
        return geneExecutor.runLoad(geneData);
    }

}
