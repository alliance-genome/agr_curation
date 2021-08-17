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

    @Inject
    DoTermService doTermService;

    @Inject
    ReferenceService referenceService;

    @Inject
    GeneService geneService;

    @Override
    public String updateDiseaseAnnotation(DiseaseAnnotationMetaDataDTO annotationData) {

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update", annotationData.getData().size());
        Map<String, Object> params = new HashMap<>();
        annotationData.getData().forEach(annotation -> {
            DiseaseAnnotation g = new DiseaseAnnotation();
            Gene gene = geneService.getByIdOrCurie(annotation.getObjectId());
            DOTerm disease = doTermService.get(annotation.getDoId());

            if (gene != null) {
                g.setSubject(gene);
                g.setObject(disease);
                Reference publication = new Reference();
                publication.setCurie(annotation.getEvidence().getPublication().getPublicationId());
                if (referenceService.get(publication.getCurie()) == null) {
                    referenceService.create(publication);
                }
                g.setReferenceList(List.of(publication));
                diseaseService.create(g);
            }
        });
        ph.finishProcess();
        return "OK";

    }

}
