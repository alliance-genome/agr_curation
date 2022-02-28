package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseGeneticModifierRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class GeneDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

    @Inject
    GeneDAO geneDAO;
    @Inject
    AffectedGenomicModelDAO agmDAO;
    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;

    public GeneDiseaseAnnotation validateAnnotation(GeneDiseaseAnnotation uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

        Long id = uiEntity.getId();
        if (id == null) {
            addMessageResponse("No Gene Disease Annotation ID provided");
            throw new ApiErrorException(response);
        }
        GeneDiseaseAnnotation dbEntity = geneDiseaseAnnotationDAO.find(id);
        if (dbEntity == null) {
            addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
            throw new ApiErrorException(response);
            // do not continue validation for update if Disease Annotation ID has not been found
        }       

        Gene subject = validateSubject(uiEntity, dbEntity);
        if(subject != null) dbEntity.setSubject(subject);

        DOTerm term = validateObject(uiEntity, dbEntity);
        if(term != null) dbEntity.setObject(term);

        List<EcoTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
        if(terms != null) dbEntity.setEvidenceCodes(terms);

        DiseaseRelation relation = validateDiseaseRelation(uiEntity, dbEntity);
        if(relation != null) dbEntity.setDiseaseRelation(relation);

        List<Gene> genes = validateWith(uiEntity);
        if(genes != null) dbEntity.setWith(genes);

        if(uiEntity.getNegated() != null) {
            dbEntity.setNegated(uiEntity.getNegated());
        }else{
            dbEntity.setNegated(false);
        }
        
        if (uiEntity.getAnnotationType() != null)
            dbEntity.setAnnotationType(uiEntity.getAnnotationType());

        String dataProvider = validateDataProvider(uiEntity);
        if (dataProvider != null) dbEntity.setDataProvider(uiEntity.getDataProvider());
    
        if (uiEntity.getSecondaryDataProvider() != null)
            dbEntity.setSecondaryDataProvider(uiEntity.getSecondaryDataProvider());
    
        BiologicalEntity diseaseGeneticModifier = validateDiseaseGeneticModifier(uiEntity);
        if (diseaseGeneticModifier != null) dbEntity.setDiseaseGeneticModifier(diseaseGeneticModifier);
    
        DiseaseGeneticModifierRelation dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity);
        if (dgmRelation != null) dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);

        AffectedGenomicModel sgdStrainBackground = validateSgdStrainBackground(uiEntity);
        if (sgdStrainBackground != null) dbEntity.setSgdStrainBackground(uiEntity.getSgdStrainBackground());
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }

        return dbEntity;
    }

    private Gene validateSubject(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
        if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isEmpty(uiEntity.getSubject().getCurie())) {
            addMessageResponse("subject", requiredMessage);
            return null;
        }
        Gene subjectEntity = geneDAO.find(uiEntity.getSubject().getCurie());
        if (subjectEntity == null) {
            addMessageResponse("subject", invalidMessage);
            return null;
        }
        return subjectEntity;

    }
    
    private DiseaseRelation validateDiseaseRelation(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
        String field = "diseaseRelation";
        if (StringUtils.isEmpty(uiEntity.getDiseaseRelation().toString())) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        DiseaseRelation relation = uiEntity.getDiseaseRelation();

        if(relation == DiseaseRelation.is_implicated_in || relation == DiseaseRelation.is_marker_for) {
            return relation;
        } else {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        
    }
    
    private AffectedGenomicModel validateSgdStrainBackground(GeneDiseaseAnnotation uiEntity) {
        if (uiEntity.getSgdStrainBackground() == null) {
            return null;
        }
        
        AffectedGenomicModel sgdStrainBackground = agmDAO.find(uiEntity.getSgdStrainBackground().getCurie());
        if (sgdStrainBackground == null) {
            addMessageResponse("sgdStrainBackground", invalidMessage);
            return null;
        }
        
        return sgdStrainBackground;
    }
}
