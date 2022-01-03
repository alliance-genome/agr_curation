package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.interfaces.crud.DiseaseAnnotationCrudInterface;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

@RequestScoped
public class DiseaseAnnotationCrudController extends BaseCrudController<DiseaseAnnotationService, DiseaseAnnotation, DiseaseAnnotationDAO> implements DiseaseAnnotationCrudInterface {

    @Inject DiseaseAnnotationService diseaseAnnotationService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(diseaseAnnotationService);
    }

    @Override
    public String updateDiseaseAnnotations(String taxonID, DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad(taxonID, annotationData);
        return "OK";
    }

    @Override
    public String updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:7955", annotationData);
        return "OK";
    }

    @Override
    public String updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:10090", annotationData);
        return "OK";
    }

    @Override
    public String updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:10116", annotationData);
        return "OK";
    }

    @Override
    public String updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:7227", annotationData);
        return "OK";
    }

    @Override
    public String updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:6239", annotationData);
        return "OK";
    }

    @Override
    public String updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:9606", annotationData);
        return "OK";
    }

    @Override
    public String updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseAnnotationService.runLoad("NCBITaxon:559292", annotationData);
        return "OK";
    }

}
