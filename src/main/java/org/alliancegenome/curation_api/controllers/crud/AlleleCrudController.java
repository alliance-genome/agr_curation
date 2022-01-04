package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleCrudInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadJobExecutor;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleMetaDataDTO;
import org.alliancegenome.curation_api.services.AlleleService;

@RequestScoped
public class AlleleCrudController extends BaseCrudController<AlleleService, Allele, AlleleDAO> implements AlleleCrudInterface {

    @Inject AlleleService alleleService;
    
    @Inject BulkLoadJobExecutor bulkLoadJobExecutor;

    @Override
    @PostConstruct
    protected void init() {
        setService(alleleService);
    }

    @Override
    public String updateAlleles(AlleleMetaDataDTO alleleData) {
        bulkLoadJobExecutor.processAlleleDTOData(null, alleleData);
        return "OK";
    }

}
