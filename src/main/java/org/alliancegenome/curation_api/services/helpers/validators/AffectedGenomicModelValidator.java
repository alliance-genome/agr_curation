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
public class AffectedGenomicModelValidator extends GenomicEntityValidator {
    
    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    
    public AffectedGenomicModel validateAnnotation(AffectedGenomicModel uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        
        String curie = validateCurie(uiEntity);
        if (curie == null) {
            throw new ApiErrorException(response);
        }
        
        AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(curie);
        if (dbEntity == null) {
            addMessageResponse("Could not find AGM with curie: [" + curie + "]");
            throw new ApiErrorException(response);
        }
        

        String errorTitle = "Could not update AGM [" + curie + "]";
        
        String name = validateName(uiEntity);
        if (name != null) dbEntity.setName(name);
        
        NCBITaxonTerm taxon = validateTaxon(uiEntity);
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

}
