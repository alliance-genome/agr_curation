package org.alliancegenome.curation_api.bulk.controllers;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.DiseaseAnnotationBulkRESTInterface;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationBulkController implements DiseaseAnnotationBulkRESTInterface {

    @Inject DiseaseAnnotationService diseaseService;

    @Override
    public String updateDiseaseAnnotation(DiseaseAnnotationMetaDataDTO annotationData) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update", annotationData.getData().size());
        Map<String, Object> params = new HashMap<>();
        annotationData.getData().forEach(annotation -> {
            diseaseService.upsert(annotation.getObjectId(), annotation.getDoId(), annotation.getEvidence().getPublication().getPublicationId());
            ph.progressProcess();
        });
        ph.finishProcess();
        return "OK";

    }

}
