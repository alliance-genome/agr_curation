package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator {
    
    @Inject
    AlleleDAO alleleDAO;
    
    public Allele validateAnnotation(Allele uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        
        String curie = validateCurie(uiEntity);
        if (curie == null) {
            throw new ApiErrorException(response);
        }
        
        Allele dbEntity = alleleDAO.find(curie);
        if (dbEntity == null) {
            addMessageResponse("Could not find allele with curie: [" + curie + "]");
            throw new ApiErrorException(response);
        }
        

        String errorTitle = "Could not update allele [" + curie + "]";
        
        NCBITaxonTerm taxon = validateTaxon(uiEntity);
        if (taxon != null) dbEntity.setTaxon(taxon);
        
        String symbol = validateSymbol(uiEntity);
        if (symbol != null) dbEntity.setSymbol(symbol);
        
        if (uiEntity.getSynonyms() != null) {
            dbEntity.setSynonyms(uiEntity.getSynonyms());
        }

        if (uiEntity.getSecondaryIdentifiers() != null) {
            dbEntity.setSecondaryIdentifiers(uiEntity.getSecondaryIdentifiers());
        }

        if (uiEntity.getCrossReferences() != null) {
            dbEntity.setCrossReferences(uiEntity.getCrossReferences());
        }
        
        if (uiEntity.getGenomicLocations() != null) {
            dbEntity.setGenomicLocations(uiEntity.getGenomicLocations());
        }
        
        if (uiEntity.getDescription() != null) {
            dbEntity.setDescription(uiEntity.getDescription());
        }
        
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }
        
        return dbEntity;
    }
    
    private String validateSymbol(Allele uiEntity) {
        String symbol = uiEntity.getSymbol();
        if (StringUtils.isEmpty(symbol)) {
            addMessageResponse("symbol", requiredMessage);
            return null;
        }
        return symbol;
    }

}
