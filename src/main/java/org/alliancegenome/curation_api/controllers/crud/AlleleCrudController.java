package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleFmsExecutor;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AlleleMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AlleleService;

@RequestScoped
public class AlleleCrudController extends BaseCrudController<AlleleService, Allele, AlleleDAO> implements AlleleCrudInterface {

    @Inject AlleleService alleleService;
    
    @Inject AlleleFmsExecutor alleleFmsExecutor;

    @Override
    @PostConstruct
    protected void init() {
        setService(alleleService);
    }

    @Override
    public APIResponse updateAlleles(AlleleMetaDataFmsDTO alleleData) {
        return alleleFmsExecutor.runLoad(alleleData);
    }

}
