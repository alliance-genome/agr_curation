package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleDiseaseAnnotationCrudService extends BaseCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDAO> {

    @Inject
    AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    
    @Inject
    AlleleDAO alleleDAO;
    
    @Inject
    AlleleDiseaseAnnotationValidator alleleDiseaseValidator;
    
    @Inject
    DiseaseAnnotationService diseaseAnnotationService;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation uiEntity) {
        log.info(authenticatedPerson);
        AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<AlleleDiseaseAnnotation>(alleleDiseaseAnnotationDAO.persist(dbEntity));
    }

    @Transactional
    public void runLoad(String taxonId, List<AlleleDiseaseAnnotationDTO> annotations) {
        List<String> annotationIdsBefore = new ArrayList<>();
        annotationIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIds(taxonId));
        annotationIdsBefore.removeIf(Objects::isNull);
        
        log.debug("runLoad: Before: " + taxonId + " " + annotationIdsBefore.size());
        List<String> annotationIdsAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Allele Disease Annotation Update " + taxonId, annotations.size());
        annotations.forEach(annotationDTO -> {
            AlleleDiseaseAnnotation annotation = upsert(annotationDTO);
            if (annotation != null) {
                annotationIdsAfter.add(annotation.getUniqueId());
            }
            ph.progressProcess();
        });
        ph.finishProcess();
        
        diseaseAnnotationService.removeNonUpdatedAnnotations(taxonId, annotationIdsBefore, annotationIdsAfter);
    }

    private AlleleDiseaseAnnotation upsert(AlleleDiseaseAnnotationDTO dto) {
        AlleleDiseaseAnnotation annotation = validateAlleleDiseaseAnnotationDTO(dto);
        if (annotation == null) return null;
        
        annotation = (AlleleDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
        if (annotation != null) {
            alleleDiseaseAnnotationDAO.persist(annotation);
        }
        return annotation;
    }
    
    private AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto) {
        AlleleDiseaseAnnotation annotation;
        if (dto.getSubject() == null) {
            log("Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
            return null;
        }
        
        Allele allele = alleleDAO.find(dto.getSubject());
        if (allele == null) {
            log("Allele " + dto.getSubject() + " not found in database - skipping annotation");
            return null;
        }
        
        String annotationId = dto.getModId();
        if (annotationId == null) {
            annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(allele.getTaxon().getCurie()).getCurieID(dto);
        }
        SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
        if (annotationList == null || annotationList.getResults().size() == 0) {
            annotation = new AlleleDiseaseAnnotation();
            annotation.setUniqueId(annotationId);
            annotation.setSubject(allele);
        } else {
            annotation = annotationList.getResults().get(0);
        }
        
        if (!dto.getDiseaseRelation().equals("is_implicated_in")) {
            log("Invalid allele disease relation for " + annotationId + " - skipping");
            return null;
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
        
        
        return (AlleleDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
    }
    
    private void log(String message) {
        log.debug(message);
        // log.info(message);
    }
}
