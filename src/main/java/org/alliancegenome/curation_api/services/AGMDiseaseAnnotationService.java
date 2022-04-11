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
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AGMDiseaseAnnotationService extends BaseCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

    @Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    @Inject VocabularyTermDAO vocabularyTermDAO;
    @Inject AffectedGenomicModelDAO agmDAO;
    @Inject NoteDAO noteDAO;
    @Inject AGMDiseaseAnnotationValidator agmDiseaseValidator;
    @Inject DiseaseAnnotationService diseaseAnnotationService;
    @Inject ConditionRelationDAO conditionRelationDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(agmDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
        AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotation(uiEntity);
        if (CollectionUtils.isNotEmpty(dbEntity.getRelatedNotes())) {
            for (Note note : dbEntity.getRelatedNotes()) {
                noteDAO.persist(note);
            }
        }
        if (CollectionUtils.isNotEmpty(dbEntity.getConditionRelations())) {
            for (ConditionRelation conditionRelation : dbEntity.getConditionRelations()) {
                conditionRelationDAO.persist(conditionRelation);
            }
        }
        
        ObjectResponse<AGMDiseaseAnnotation> response =
                new ObjectResponse<AGMDiseaseAnnotation>(agmDiseaseAnnotationDAO.persist(dbEntity));
        if (response.getEntity().getObject().getCrossReferences() != null)
            response.getEntity().getObject().getCrossReferences().size();
        if (response.getEntity().getSubject().getTaxon().getSubsets() != null)
            response.getEntity().getSubject().getTaxon().getSubsets().size();
        if (response.getEntity().getSubject().getTaxon().getCrossReferences() != null)
            response.getEntity().getSubject().getTaxon().getCrossReferences().size();
        if (response.getEntity().getSubject().getTaxon().getSecondaryIdentifiers() != null)
            response.getEntity().getSubject().getTaxon().getSecondaryIdentifiers().size();
        if (response.getEntity().getSubject().getTaxon().getSynonyms() != null)
            response.getEntity().getSubject().getTaxon().getSynonyms().size();
        if (response.getEntity().getSubject().getSynonyms() != null)
            response.getEntity().getSubject().getSynonyms().size();
        if (response.getEntity().getSubject().getCrossReferences() != null)
            response.getEntity().getSubject().getCrossReferences().size();
        for (EcoTerm ecoTerm : response.getEntity().getEvidenceCodes()) {
            if (ecoTerm.getCrossReferences() != null)
                ecoTerm.getCrossReferences().size();    
        }
        for (Gene gene : response.getEntity().getWith()) {
            if (gene.getTaxon().getSubsets() != null)
                gene.getTaxon().getSubsets().size();
            if (gene.getTaxon().getCrossReferences() != null)
                gene.getTaxon().getCrossReferences().size();
            if (gene.getTaxon().getSecondaryIdentifiers() != null)
                gene.getTaxon().getSecondaryIdentifiers().size();
            if (gene.getTaxon().getSynonyms() != null)
                gene.getTaxon().getSynonyms().size();
            if (gene.getSynonyms() != null)
                gene.getSynonyms().size();
            if (gene.getCrossReferences() != null)
                gene.getCrossReferences().size();
        }
        return response;
    }
    

    @Transactional
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
        
        String annotationId = dto.getModEntityId();
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
        
        VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY);
        if (diseaseRelation == null) {
            throw new ObjectUpdateException(dto, "Invalid AGM disease relation for " + annotationId + " - skipping");
        }
        annotation.setDiseaseRelation(diseaseRelation);
        
        
        return annotation;
    }

    
}
