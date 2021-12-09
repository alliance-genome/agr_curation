package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;

public class DiseaseAnnotationValidator {

    @Inject
    EcoTermDAO ecoTermDAO;
    @Inject
    ReferenceDAO referenceDAO;
    @Inject
    DoTermDAO doTermDAO;
    @Inject
    GeneDAO geneDAO;
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    
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
    
    
    public List<Gene> validateWith(DiseaseAnnotation  uiEntity, DiseaseAnnotation  dbEntity) {
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
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
