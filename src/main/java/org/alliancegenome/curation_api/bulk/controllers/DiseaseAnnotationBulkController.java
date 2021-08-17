package org.alliancegenome.curation_api.bulk.controllers;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.interfaces.bulk.DiseaseAnnotationBulkRESTInterface;
import org.alliancegenome.curation_api.model.dto.json.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DoTermService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JBossLog
@RequestScoped
public class DiseaseAnnotationBulkController implements DiseaseAnnotationBulkRESTInterface {

    @Inject
    DiseaseAnnotationService diseaseService;

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
