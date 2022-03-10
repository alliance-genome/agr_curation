package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseGeneticModifierRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class DiseaseAnnotationValidator {

    @Inject
    EcoTermDAO ecoTermDAO;
    @Inject
    DoTermDAO doTermDAO;
    @Inject
    GeneDAO geneDAO;
    @Inject
    BiologicalEntityDAO biologicalEntityDAO;
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    protected String dependencyMessagePrefix = "Invalid without value for ";
    
    protected ObjectResponse<DiseaseAnnotation > response;
    
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
        String dataProvider = uiEntity.getDataProvider();
        if (dataProvider == null) {
            addMessageResponse("dataProvider", requiredMessage);
            return null;
        }
        
        return dataProvider;
    }
    
    public String validateCreatedBy(DiseaseAnnotation uiEntity) {
        String createdBy = uiEntity.getCreatedBy();
        if (createdBy == null) {
            addMessageResponse("createdBy", requiredMessage);
            return null;
        }
        
        return createdBy;
    }
    
    public String validateModifiedBy(DiseaseAnnotation uiEntity) {
        String modifiedBy = uiEntity.getModifiedBy();
        if (modifiedBy == null) {
            addMessageResponse("modifiedBy", requiredMessage);
            return null;
        }
        
        return modifiedBy;
    }
    
    public BiologicalEntity validateDiseaseGeneticModifier(DiseaseAnnotation uiEntity) {
        if (uiEntity.getDiseaseGeneticModifier() == null) {
            return null;
        }

        if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
            addMessageResponse("diseaseGeneticModifer", dependencyMessagePrefix + "diseaseGeneticModifierRelation");
            return null;
        }
        
        BiologicalEntity modifier = biologicalEntityDAO.find(uiEntity.getDiseaseGeneticModifier().getCurie());
        if (modifier == null) {
            addMessageResponse("diseaseGeneticModifier", invalidMessage);
            return null;
        }
        
        return modifier;
    }
    
    public DiseaseGeneticModifierRelation validateDiseaseGeneticModifierRelation(DiseaseAnnotation uiEntity) {
        if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
            return null;
        }
        
        if (uiEntity.getDiseaseGeneticModifier() == null) {
            addMessageResponse("diseaseGeneticModifierRelation", dependencyMessagePrefix + "diseaseGeneticModifier");
            return null;
        }
        
        return uiEntity.getDiseaseGeneticModifierRelation();
    }
    
    public DiseaseAnnotation validateCommonDiseaseAnnotationFields(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
        
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

        String createdBy = validateCreatedBy(uiEntity);
        if (createdBy != null) dbEntity.setCreatedBy(createdBy);

        String modifiedBy = validateModifiedBy(uiEntity);
        if (modifiedBy != null) dbEntity.setModifiedBy(modifiedBy);
    
        if (uiEntity.getCreationDate() != null)
            dbEntity.setCreationDate(uiEntity.getCreationDate());
        
        if (uiEntity.getDateLastModified() != null)
            dbEntity.setDateLastModified(uiEntity.getDateLastModified());
        
        if (uiEntity.getSecondaryDataProvider() != null)
            dbEntity.setSecondaryDataProvider(uiEntity.getSecondaryDataProvider());
    
        BiologicalEntity diseaseGeneticModifier = validateDiseaseGeneticModifier(uiEntity);
        DiseaseGeneticModifierRelation dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity);
        if (diseaseGeneticModifier != null && dgmRelation != null) {
            dbEntity.setDiseaseGeneticModifier(diseaseGeneticModifier);
            dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);
        }
        
        if (CollectionUtils.isNotEmpty(uiEntity.getDiseaseQualifiers()))
            dbEntity.setDiseaseQualifiers(uiEntity.getDiseaseQualifiers());
        
        // TODO: Add validation of reference
        if (uiEntity.getSingleReference() != null)
            dbEntity.setSingleReference(uiEntity.getSingleReference());
        
        return dbEntity;
    }
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
