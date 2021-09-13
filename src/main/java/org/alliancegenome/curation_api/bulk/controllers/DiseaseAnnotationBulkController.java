package org.alliancegenome.curation_api.bulk.controllers;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.interfaces.bulk.DiseaseAnnotationBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@JBossLog
@RequestScoped
public class DiseaseAnnotationBulkController implements DiseaseAnnotationBulkRESTInterface {

    @Inject
    DiseaseAnnotationService diseaseService;

    @Override
    public String updateDiseaseAnnotation(String taxonID, DiseaseAnnotationMetaDataDTO annotationData) {
        diseaseService.runLoad(taxonID, annotationData);
        return "OK";

    }


}
