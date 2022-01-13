package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

public class GenomicEntityValidator {

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
        String taxon = uiEntity.getTaxon();
        if (StringUtils.isEmpty(taxon)) {
            addMessageResponse("taxon", requiredMessage);
            return null;
        }
        //TODO: replace regex matching with database lookup once taxons loaded
        Pattern taxonIdPattern = Pattern.compile("^NCBITaxon:\\d+$");
        Matcher taxonIdMatcher = taxonIdPattern.matcher(taxon);
        if (!taxonIdMatcher.find()) {
            return null;
        }
        return taxon;
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
