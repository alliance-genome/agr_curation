package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.DiseaseAnnotationFmsCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.DiseaseAnnotationFmsExecutor;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.DiseaseAnnotationFmsService;

@RequestScoped
public class DiseaseAnnotationFmsCrudController extends BaseCrudController<DiseaseAnnotationFmsService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationFmsCrudInterface {

    @Inject DiseaseAnnotationFmsService diseaseAnnotationService;
    @Inject DiseaseAnnotationFmsExecutor diseaseAnnotationFmsExecutor;

    @Override
    @PostConstruct
    protected void init() {
        setService(diseaseAnnotationService);
    }

    @Override
    public APIResponse updateDiseaseAnnotations(String taxonID, DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad(taxonID, annotations);
    }

    @Override
    public APIResponse updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:7955", annotations);
    }

    @Override
    public APIResponse updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:10090", annotations);
    }

    @Override
    public APIResponse updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:10116", annotations);
    }

    @Override
    public APIResponse updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:7227", annotations);
    }

    @Override
    public APIResponse updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:6239", annotations);
    }

    @Override
    public APIResponse updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:9606", annotations);
    }

    @Override
    public APIResponse updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        return diseaseAnnotationFmsExecutor.runLoad("NCBITaxon:559292", annotations);
    }

}
