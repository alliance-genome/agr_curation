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
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AlleleDiseaseAnnotationService extends BaseCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDAO> {

    @Inject
    AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    @Inject
    AlleleDAO alleleDAO;
    @Inject
    NoteDAO noteDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    AlleleDiseaseAnnotationValidator alleleDiseaseValidator;
    @Inject
    DiseaseAnnotationService diseaseAnnotationService;
    @Inject
    ConditionRelationDAO conditionRelationDAO;
    

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation uiEntity) {
        AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotation(uiEntity);
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
        
        ObjectResponse<AlleleDiseaseAnnotation> response =
                new ObjectResponse<AlleleDiseaseAnnotation>(alleleDiseaseAnnotationDAO.persist(dbEntity));
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
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject Allele - skipping");
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
        
        VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY);
        if (diseaseRelation == null) {
            throw new ObjectValidationException(dto, "Invalid allele disease relation for " + annotationId + " - skipping");
        }
        annotation.setDiseaseRelation(diseaseRelation);
        
        
        return annotation;
    }

}
