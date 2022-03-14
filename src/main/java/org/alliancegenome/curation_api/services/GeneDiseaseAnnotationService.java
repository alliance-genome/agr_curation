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
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.GeneDiseaseAnnotationValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneDiseaseAnnotationService extends BaseCrudService<GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> {

    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    
    @Inject
    GeneDAO geneDAO;
    
    @Inject
    GeneDiseaseAnnotationValidator geneDiseaseValidator;

    @Inject
    DiseaseAnnotationService diseaseAnnotationService;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation uiEntity) {
        GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<GeneDiseaseAnnotation>(geneDiseaseAnnotationDAO.persist(dbEntity));
    }

    public GeneDiseaseAnnotation upsert(GeneDiseaseAnnotationDTO dto) throws ObjectUpdateException {
        GeneDiseaseAnnotation annotation = validateGeneDiseaseAnnotationDTO(dto);

        annotation = (GeneDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
        if (annotation != null) {
            geneDiseaseAnnotationDAO.persist(annotation);
        }
        return annotation;
    }
    
    private GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto) throws ObjectValidationException {
        GeneDiseaseAnnotation annotation;
        if (dto.getSubject() == null) {
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
        }
        
        Gene gene = geneDAO.find(dto.getSubject());
        if (gene == null) {
            throw new ObjectValidationException(dto, "Allele " + dto.getSubject() + " not found in database - skipping annotation");
        }
        
        String annotationId = dto.getModId();
        if (annotationId == null) {
            annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(gene.getTaxon().getCurie()).getCurieID(dto);
        }
        SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
        if (annotationList == null || annotationList.getResults().size() == 0) {
            annotation = new GeneDiseaseAnnotation();
            annotation.setUniqueId(annotationId);
            annotation.setSubject(gene);
        } else {
            annotation = annotationList.getResults().get(0);
        }
        
        annotation = (GeneDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
        if (annotation == null) return null;
        
        if (!dto.getDiseaseRelation().equals("is_implicated_in") && !dto.getDiseaseRelation().equals("is_marker_for")) {
            throw new ObjectValidationException(dto, "Invalid gene disease relation for " + annotationId + " - skipping");
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
        
        return annotation;
    }

}
