package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class DiseaseAnnotationValidator extends AuditedObjectValidator<DiseaseAnnotation>{

    @Inject
    EcoTermDAO ecoTermDAO;
    @Inject
    DoTermDAO doTermDAO;
    @Inject
    GeneDAO geneDAO;
    @Inject
    BiologicalEntityDAO biologicalEntityDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    ReferenceDAO referenceDAO;
    @Inject
    ReferenceService referenceService;
    @Inject
    NoteValidator noteValidator;
    @Inject
    ConditionRelationValidator conditionRelationValidator;
    
    public DOTerm validateObject(DiseaseAnnotation  uiEntity, DiseaseAnnotation  dbEntity) {
        String field = "object";
        if (ObjectUtils.isEmpty(uiEntity.getObject()) || StringUtils.isEmpty(uiEntity.getObject().getCurie())) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        DOTerm diseaseTerm = doTermDAO.find(uiEntity.getObject().getCurie());
        if (diseaseTerm == null) {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        else if (diseaseTerm.getObsolete() && !diseaseTerm.getCurie().equals(dbEntity.getObject().getCurie())) {
            addMessageResponse(field, obsoleteMessage);
            return null;
        }
        return diseaseTerm;
    }

    
    public List<EcoTerm> validateEvidenceCodes(DiseaseAnnotation  uiEntity, DiseaseAnnotation  dbEntity) {
        String field = "evidence";
        if (CollectionUtils.isEmpty(uiEntity.getEvidenceCodes())) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        List<EcoTerm> validEvidenceCodes = new ArrayList<>(); 
        for (EcoTerm ec : uiEntity.getEvidenceCodes()) {
            EcoTerm evidenceCode = ecoTermDAO.find(ec.getCurie());
            if (evidenceCode == null ) {
                addMessageResponse(field, invalidMessage);
                return null;
            }
            else if (evidenceCode.getObsolete() && !dbEntity.getEvidenceCodes().contains(evidenceCode)) {
                addMessageResponse(field, obsoleteMessage);
                return null;
            }

            validEvidenceCodes.add(evidenceCode);

        }
        return validEvidenceCodes;
    }
    
    
    public List<Gene> validateWith(DiseaseAnnotation  uiEntity) {
        List<Gene> validWithGenes = new ArrayList<Gene>();
        
        if (CollectionUtils.isNotEmpty(uiEntity.getWith())) {
            for (Gene wg : uiEntity.getWith()) {
                Gene withGene = geneDAO.find(wg.getCurie());
                if (withGene == null || !withGene.getCurie().startsWith("HGNC:")) {
                    addMessageResponse("with", invalidMessage);
                    return null;
                }
                else {
                    validWithGenes.add(withGene);
                }
            }
        }
        
        return validWithGenes;
    }
    
    public String validateDataProvider(DiseaseAnnotation uiEntity) {
        // TODO: re-enable error response once field can be added in UI
        String dataProvider = uiEntity.getDataProvider();
        if (dataProvider == null) {
            // addMessageResponse("dataProvider", requiredMessage);
            return null;
        }
        
        return dataProvider;
    }
    
    public Person validateCreatedBy(DiseaseAnnotation uiEntity) {
        // TODO: re-enable error response once field can be added in UI
        Person createdBy = uiEntity.getCreatedBy();
        if (createdBy == null) {
            // addMessageResponse("createdBy", requiredMessage);
            return null;
        }
        
        return createdBy;
    }
    
    public BiologicalEntity validateDiseaseGeneticModifier(DiseaseAnnotation uiEntity) {
        if (uiEntity.getDiseaseGeneticModifier() == null) {
            return null;
        }

        if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
            addMessageResponse("diseaseGeneticModifier", dependencyMessagePrefix + "diseaseGeneticModifierRelation");
            return null;
        }
        
        BiologicalEntity modifier = biologicalEntityDAO.find(uiEntity.getDiseaseGeneticModifier().getCurie());
        if (modifier == null) {
            addMessageResponse("diseaseGeneticModifier", invalidMessage);
            return null;
        }
        
        return modifier;
    }
    
    public VocabularyTerm validateDiseaseGeneticModifierRelation(DiseaseAnnotation uiEntity) {
        if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
            return null;
        }
        
        if (uiEntity.getDiseaseGeneticModifier() == null) {
            addMessageResponse("diseaseGeneticModifierRelation", dependencyMessagePrefix + "diseaseGeneticModifier");
            return null;
        }
        
        return uiEntity.getDiseaseGeneticModifierRelation();
    }
    
    public List<Note> validateRelatedNotes(DiseaseAnnotation uiEntity) {
        List<Note> validatedNotes = new ArrayList<Note>();
        for (Note note : uiEntity.getRelatedNotes()) {
            ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
            if (noteResponse.getEntity() == null) {
                Map<String, String> errors = noteResponse.getErrorMessages();
                for (String field : errors.keySet()) {
                    addMessageResponse("relatedNotes", field + " - " + errors.get(field));
                }
                return null;
            }
            validatedNotes.add(noteResponse.getEntity());
        }
        return validatedNotes;
    }
    
    public List<ConditionRelation> validateConditionRelations(DiseaseAnnotation uiEntity) {
        List<ConditionRelation> validatedConditionRelations = new ArrayList<ConditionRelation>();
        for (ConditionRelation conditionRelation : uiEntity.getConditionRelations()) {
            ObjectResponse<ConditionRelation> crResponse = conditionRelationValidator.validateConditionRelation(conditionRelation);
            if (crResponse.getEntity() == null) {
                Map<String, String> errors = crResponse.getErrorMessages();
                for (String field : errors.keySet()) {
                    addMessageResponse("conditionRelations", field + " - " + errors.get(field));
                }
                return null;
            }
            validatedConditionRelations.add(crResponse.getEntity());
        }
        return validatedConditionRelations;
    }
    
    public Reference validateSingleReference(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
        String field = "singleReference";
        if (ObjectUtils.isEmpty(uiEntity.getSingleReference()) || StringUtils.isEmpty(uiEntity.getSingleReference().getCurie())) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        Reference singleReference = referenceDAO.find(uiEntity.getSingleReference().getCurie());
        if (singleReference == null) {
            singleReference = referenceService.retrieveFromLiteratureService(uiEntity.getSingleReference().getCurie());
            if (singleReference == null) {
                addMessageResponse(field, invalidMessage);
                return null;
            }
        }
        if (singleReference.getObsolete() && !singleReference.getCurie().equals(dbEntity.getSingleReference().getCurie())) {
            addMessageResponse(field, obsoleteMessage);
            return null;
        }
        return singleReference;
    }
    
    public DiseaseAnnotation validateCommonDiseaseAnnotationFields(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
        dbEntity = validateAuditedObjectFields(uiEntity, dbEntity);
        
        if (uiEntity.getModEntityId() != null)
            dbEntity.setModEntityId(uiEntity.getModEntityId());

        DOTerm term = validateObject(uiEntity, dbEntity);
        if(term != null) dbEntity.setObject(term);

        List<EcoTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
        if(terms != null) dbEntity.setEvidenceCodes(terms);

        List<Gene> genes = validateWith(uiEntity);
        if(genes != null) dbEntity.setWith(genes);

        if(uiEntity.getNegated() != null) {
            dbEntity.setNegated(uiEntity.getNegated());
        }else{
            dbEntity.setNegated(false);
        }
        
        if (uiEntity.getAnnotationType() != null)
            dbEntity.setAnnotationType(uiEntity.getAnnotationType());

        if (uiEntity.getGeneticSex() != null)
            dbEntity.setGeneticSex(uiEntity.getGeneticSex());

        String dataProvider = validateDataProvider(uiEntity);
        if (dataProvider != null) dbEntity.setDataProvider(dataProvider);

        Person createdBy = validateCreatedBy(uiEntity);
        if (createdBy != null) dbEntity.setCreatedBy(createdBy);

        if (uiEntity.getSecondaryDataProvider() != null)
            dbEntity.setSecondaryDataProvider(uiEntity.getSecondaryDataProvider());
    
        BiologicalEntity diseaseGeneticModifier = validateDiseaseGeneticModifier(uiEntity);
        VocabularyTerm dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity);
        if (diseaseGeneticModifier != null && dgmRelation != null) {
            dbEntity.setDiseaseGeneticModifier(diseaseGeneticModifier);
            dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);
        }
        
        if (CollectionUtils.isNotEmpty(uiEntity.getConditionRelations())) {
            List<ConditionRelation> conditionRelations = validateConditionRelations(uiEntity);
            dbEntity.setConditionRelations(conditionRelations);
        }
        
        if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
            List<Note> relatedNotes = validateRelatedNotes(uiEntity);
            if (relatedNotes != null) dbEntity.setRelatedNotes(relatedNotes);
        }
        
        if (CollectionUtils.isNotEmpty(uiEntity.getDiseaseQualifiers()))
            dbEntity.setDiseaseQualifiers(uiEntity.getDiseaseQualifiers());
        
        Reference singleReference = validateSingleReference(uiEntity, dbEntity);
        if (singleReference != null)
            dbEntity.setSingleReference(singleReference);
        
        return dbEntity;
    }
}
