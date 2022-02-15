package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneFmsExecutor;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.fms.dto.GeneMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.GeneService;

@RequestScoped
public class GeneCrudController extends BaseCrudController<GeneService, Gene, GeneDAO> implements GeneCrudInterface {

    @Inject GeneService geneService;
    
    @Inject GeneFmsExecutor geneFmsExecutor;
    
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
    public APIResponse updateGenes(GeneMetaDataFmsDTO geneData) {
        return geneFmsExecutor.runLoad(geneData);
    }

}
