package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.DiseaseAnnotationBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationBulkController implements DiseaseAnnotationBulkRESTInterface {

    @Inject
    DiseaseAnnotationService diseaseService;

    @Override
    public String updateDiseaseAnnotations(String taxonID, DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad(taxonID, annotationData);
        return "OK";
    }

    @Override
    public String updateZFinDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:7955", annotationData);
        return "OK";
    }

    @Override
    public String updateMgiDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:10090", annotationData);
        return "OK";
    }

    @Override
    public String updateRgdDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:10116", annotationData);
        return "OK";
    }

    @Override
    public String updateFBDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:7227", annotationData);
        return "OK";
    }

    @Override
    public String updateWBDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:6239", annotationData);
        return "OK";
    }

    @Override
    public String updateHUMANDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:9606", annotationData);
        return "OK";
    }

    @Override
    public String updateSGDDiseaseAnnotations(DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad("NCBITaxon:559292", annotationData);
        return "OK";
    }
}
