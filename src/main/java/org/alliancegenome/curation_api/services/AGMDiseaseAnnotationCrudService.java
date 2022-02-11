package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AGMDiseaseAnnotationCrudService extends BaseCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    
    @Inject
    AffectedGenomicModelDAO agmDAO;
    
    @Inject
    AGMDiseaseAnnotationValidator agmDiseaseValidator;
    
    @Inject
    AGMDiseaseAnnotationCrudService agmDiseaseAnnotationService;
    
    @Inject
    DiseaseAnnotationService diseaseAnnotationService;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(agmDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
        log.info(authenticatedPerson);
        AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
    }
    
    @Transactional
    public void runLoad(String taxonId, List<AGMDiseaseAnnotationDTO> annotations) {
        List<String> annotationIdsBefore = new ArrayList<>();
        annotationIdsBefore.addAll(agmDiseaseAnnotationDAO.findAllAnnotationIds(taxonId));
        annotationIdsBefore.removeIf(Objects::isNull);
        
        log.debug("runLoad: Before: " + taxonId + " " + annotationIdsBefore.size());
        List<String> annotationIdsAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("AGM Disease Annotation Update " + taxonId, annotations.size());
        annotations.forEach(annotationDTO -> {
            AGMDiseaseAnnotation annotation = upsert(annotationDTO);
            if (annotation != null) {
                annotationIdsAfter.add(annotation.getUniqueId());
            }
            ph.progressProcess();
        });
        ph.finishProcess();
        
        diseaseAnnotationService.removeNonUpdatedAnnotations(taxonId, annotationIdsBefore, annotationIdsAfter);
    }

    private AGMDiseaseAnnotation upsert(AGMDiseaseAnnotationDTO dto) {
        AGMDiseaseAnnotation annotation = validateAGMDiseaseAnnotationDTO(dto);
        if (annotation == null) return null;
        
        annotation = (AGMDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
        if (annotation != null) {
            agmDiseaseAnnotationDAO.persist(annotation);
        }
        return annotation;
    }
    
    private AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto) {
        AGMDiseaseAnnotation annotation;
        if (dto.getSubject() == null) {
            log("Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
            return null;
        }
        
        AffectedGenomicModel agm = agmDAO.find(dto.getSubject());
        if (agm == null) {
            log("AGM " + dto.getSubject() + " not found in database - skipping annotation");
            return null;
        }
        
        String annotationId = dto.getModId();
        if (annotationId == null) {
            annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(agm.getTaxon().getCurie()).getCurieID(dto);
        }
        SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
        if (annotationList == null || annotationList.getResults().size() == 0) {
            annotation = new AGMDiseaseAnnotation();
            annotation.setUniqueId(annotationId);
            annotation.setSubject(agm);
        } else {
            annotation = annotationList.getResults().get(0);
        }
        
        if (!dto.getDiseaseRelation().equals("is_model_of")) {
            log("Invalid AGM disease relation for " + annotationId + " - skipping");
            return null;
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
        
        
        return (AGMDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
    }
    
    private void log(String message) {
        log.debug(message);
        // log.info(message);
    }
    
}
