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
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleDiseaseAnnotationValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleDiseaseAnnotationService extends BaseCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDAO> {

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

    public AlleleDiseaseAnnotation upsert(AlleleDiseaseAnnotationDTO dto) throws ObjectUpdateException {
        AlleleDiseaseAnnotation annotation = validateAlleleDiseaseAnnotationDTO(dto);
        if (annotation == null) return null;
        
        annotation = (AlleleDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
        if (annotation != null) {
            alleleDiseaseAnnotationDAO.persist(annotation);
        }
        return annotation;
    }
    
    private AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto) throws ObjectValidationException {
        AlleleDiseaseAnnotation annotation;
        if (dto.getSubject() == null) {
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
        }
        
        Allele allele = alleleDAO.find(dto.getSubject());
        if (allele == null) {
            throw new ObjectValidationException(dto, "Allele " + dto.getSubject() + " not found in database - skipping annotation");
        }
        
        String annotationId = dto.getModEntityId();
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
        
        annotation = (AlleleDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
        
        if (!dto.getDiseaseRelation().equals("is_implicated_in")) {
            throw new ObjectValidationException(dto, "Invalid allele disease relation for " + annotationId + " - skipping");
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
        
        
        return annotation;
    }

}
