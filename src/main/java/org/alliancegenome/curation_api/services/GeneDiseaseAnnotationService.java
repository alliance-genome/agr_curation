package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.GeneDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneDiseaseAnnotationService extends BaseCrudService<GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> {

    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    
    @Inject
    GeneDAO geneDAO;
    
    @Inject
    NoteDAO noteDAO;
    
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    
    @Inject
    GeneDiseaseAnnotationValidator geneDiseaseValidator;

    @Inject
    DiseaseAnnotationService diseaseAnnotationService;
    
    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation uiEntity) {
        GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotation(uiEntity);
        if (CollectionUtils.isNotEmpty(dbEntity.getRelatedNotes())) {
            for (Note note : dbEntity.getRelatedNotes()) {
                noteDAO.persist(note);
            }
        }
        return new ObjectResponse<GeneDiseaseAnnotation>(geneDiseaseAnnotationDAO.persist(dbEntity));
    }

    @Transactional
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
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject Gene - skipping");
        }
        Gene gene = geneDAO.find(dto.getSubject());
        if (gene == null) {
            throw new ObjectValidationException(dto, "Gene " + dto.getSubject() + " not found in database - skipping annotation");
        }
        
        String annotationId = dto.getModEntityId();
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
        
        if (dto.getSgdStrainBackground() != null) {
            AffectedGenomicModel sgdStrainBackground = affectedGenomicModelDAO.find(dto.getSgdStrainBackground());
            if (sgdStrainBackground == null) {
                throw new ObjectValidationException(dto, "Invalid AGM (" + dto.getSgdStrainBackground() + ") in 'sgd_strain_background' field in " + annotation.getUniqueId() + " - skipping annotation");
            }
            if (!sgdStrainBackground.getTaxon().getCurie().equals("NCBITaxon:559292")) {
                throw new ObjectValidationException(dto, "Non-SGD AGM (" + dto.getSgdStrainBackground() + ") found in 'sgdStrainBackground' field in " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setSgdStrainBackground(sgdStrainBackground);
        }
        
        annotation = (GeneDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
        if (annotation == null) return null;
        
        VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY);
        if (diseaseRelation == null) {
            throw new ObjectValidationException(dto, "Invalid gene disease relation for " + annotationId + " - skipping");
        }
        annotation.setDiseaseRelation(diseaseRelation);
        
        return annotation;
    }

}
