package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AGMDiseaseAnnotationService extends BaseCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

    @Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    @Inject AffectedGenomicModelDAO agmDAO;
    @Inject AGMDiseaseAnnotationValidator agmDiseaseValidator;
    @Inject DiseaseAnnotationService diseaseAnnotationService;

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

    public AGMDiseaseAnnotation upsert(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException {
        AGMDiseaseAnnotation annotation = validateAGMDiseaseAnnotationDTO(dto);
        if (annotation == null) throw new ObjectUpdateException(dto, "Validation Failed");
        
        annotation = (AGMDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
        if (annotation != null) {
            agmDiseaseAnnotationDAO.persist(annotation);
        }
        return annotation;
    }
    
    private AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException, ObjectValidationException {
        AGMDiseaseAnnotation annotation;
        if (dto.getSubject() == null) {
            throw new ObjectUpdateException(dto, "Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
        }
        
        AffectedGenomicModel agm = agmDAO.find(dto.getSubject());
        if (agm == null) {
            throw new ObjectUpdateException(dto, "AGM " + dto.getSubject() + " not found in database - skipping annotation");
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
        
        annotation = (AGMDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
        if (annotation == null) return null;
        
        if (!dto.getDiseaseRelation().equals("is_model_of")) {
            throw new ObjectUpdateException(dto, "Invalid AGM disease relation for " + annotationId + " - skipping");
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
        
        
        return annotation;
    }

    
}
