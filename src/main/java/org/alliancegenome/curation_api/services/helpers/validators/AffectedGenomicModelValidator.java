package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class AffectedGenomicModelValidator {
    
    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    
    protected String invalidMessage = "Not a valid entry";
    protected String requiredMessage = "Required field is empty";

    protected ObjectResponse<AffectedGenomicModel> response;

    public AffectedGenomicModel validateAnnotation(AffectedGenomicModel uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update AGM [" + uiEntity.getCurie() + "]";
        
        String curie = uiEntity.getCurie();
        if (curie == null) {
            addMessageResponse("No AGM curie provided");
            throw new ApiErrorException(response);
        }
        AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(curie);
        if (dbEntity == null) {
            addMessageResponse("Could not find AGM with curie: [" + curie + "]");
            throw new ApiErrorException(response);
        }
        
        String name = validateName(uiEntity);
        if (name != null) dbEntity.setName(name);
        
        String taxon = validateTaxon(uiEntity);
        if (taxon != null) dbEntity.setTaxon(taxon);
        
        if (uiEntity.getSubtype() != null) {
            dbEntity.setSubtype(uiEntity.getSubtype());
        }
        
        if (uiEntity.getSynonyms() != null) {
            dbEntity.setSynonyms(uiEntity.getSynonyms());
        }

        if (uiEntity.getSecondaryIdentifiers() != null) {
            dbEntity.setSecondaryIdentifiers(uiEntity.getSecondaryIdentifiers());
        }

        if (uiEntity.getCrossReferences() != null) {
            dbEntity.setCrossReferences(uiEntity.getCrossReferences());
        }
        
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }
        
        return dbEntity;
    }
    
    private String validateName(AffectedGenomicModel uiEntity) {
        String name = uiEntity.getName();
        if (StringUtils.isEmpty(name)) {
            addMessageResponse("name", requiredMessage);
            return null;
        }
        return name;
    }

    private String validateTaxon(AffectedGenomicModel uiEntity) {
        String taxon = uiEntity.getTaxon();
        if (StringUtils.isEmpty(taxon)) {
            addMessageResponse("taxon", requiredMessage);
            return null;
        }
        return taxon;
    }
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
