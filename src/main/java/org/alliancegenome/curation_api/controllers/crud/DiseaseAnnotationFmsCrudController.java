package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.DiseaseAnnotationFmsCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationFmsService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class DiseaseAnnotationFmsCrudController extends BaseCrudController<DiseaseAnnotationFmsService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationFmsCrudInterface {

    @Inject
    DiseaseAnnotationFmsService diseaseAnnotationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(diseaseAnnotationService);
    }

    @Override
    public String updateDiseaseAnnotations(String taxonID, DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad(taxonID, annotations);
        return "OK";
    }

    @Override
    public String updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:7955", annotations);
        return "OK";
    }

    @Override
    public String updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:10090", annotations);
        return "OK";
    }

    @Override
    public String updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:10116", annotations);
        return "OK";
    }

    @Override
    public String updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:7227", annotations);
        return "OK";
    }

    @Override
    public String updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:6239", annotations);
        return "OK";
    }

    @Override
    public String updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:9606", annotations);
        return "OK";
    }

    @Override
    public String updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataFmsDTO annotations) {
        diseaseAnnotationService.runLoad("NCBITaxon:559292", annotations);
        return "OK";
    }

}
