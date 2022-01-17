package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.*;

public class GenomicEntityValidator {

    @Inject
    NcbiTaxonTermService ncbiTaxonTermService;
    
    protected String invalidMessage = "Not a valid entry";
    protected String requiredMessage = "Required field is empty";

    protected ObjectResponse<GenomicEntity> response;
    
    public String validateCurie(GenomicEntity uiEntity) {
        String curie = uiEntity.getCurie();
        if (curie == null) {
            addMessageResponse("curie", requiredMessage);
            return null;
        }
        return curie;
    }
    
    public String validateTaxon(GenomicEntity uiEntity) {
        String taxonCurie = uiEntity.getTaxon();
        if (StringUtils.isEmpty(taxonCurie)) {
            addMessageResponse("taxon", requiredMessage);
            return null;
        }
        NCBITaxonTerm taxon = ncbiTaxonTermService.getTaxonByCurie(taxonCurie);
        if (taxon == null) return null;
        return taxon.getCurie();
    }
    
    public String validateName(GenomicEntity uiEntity) {
        String name = uiEntity.getName();
        if (StringUtils.isEmpty(name)) {
            addMessageResponse("name", requiredMessage);
            return null;
        }
        return name;
    }
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
