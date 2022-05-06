package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneValidator extends GenomicEntityValidator {
    
    @Inject
    GeneDAO geneDAO;
    @Inject
    SoTermDAO soTermDAO;
    
    public Gene validateAnnotation(Gene uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        
        String curie = validateCurie(uiEntity);
        if (curie == null) {
            throw new ApiErrorException(response);
        }
        
        Gene dbEntity = geneDAO.find(curie);
        if (dbEntity == null) {
            addMessageResponse("Could not find allele with curie: [" + curie + "]");
            throw new ApiErrorException(response);
        }
        
        String errorTitle = "Could not update allele [" + curie + "]";
        
        dbEntity = (Gene) validateAuditedObjectFields(uiEntity, dbEntity);

        String name = validateName(uiEntity);
        if (name != null) dbEntity.setName(name);
        
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
        
        if (uiEntity.getGeneSynopsis() != null) {
            dbEntity.setGeneSynopsis(uiEntity.getGeneSynopsis());
        }

        if (uiEntity.getGeneSynopsisURL() != null) {
            dbEntity.setGeneSynopsisURL(uiEntity.getGeneSynopsisURL());
        }
        
        if (uiEntity.getAutomatedGeneDescription() != null) {
            dbEntity.setAutomatedGeneDescription(uiEntity.getAutomatedGeneDescription());
        }
        
        SOTerm geneType = validateGeneType(uiEntity, dbEntity);
        if (geneType != null) dbEntity.setGeneType(geneType);
        
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }
        
        return dbEntity;
    }
    
    private String validateSymbol(Gene uiEntity) {
        String symbol = uiEntity.getSymbol();
        if (StringUtils.isEmpty(symbol)) {
            addMessageResponse("symbol", requiredMessage);
            return null;
        }
        return symbol;
    }
    
    private SOTerm validateGeneType(Gene uiEntity, Gene dbEntity) {
        SOTerm soTerm = soTermDAO.find(uiEntity.getGeneType().getCurie());
        if (soTerm == null) {
            addMessageResponse("geneType", invalidMessage);
            return null;
        }
        else if (soTerm.getObsolete() && !soTerm.getCurie().equals(dbEntity.getGeneType())) {
            addMessageResponse("geneType", obsoleteMessage);
            return null;
        }
        return soTerm;
    }

}
