package org.alliancegenome.curation_api.services.helpers.validators;

import javax.inject.Inject;

import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

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
    
    public NCBITaxonTerm validateTaxon(GenomicEntity uiEntity) {
        String taxonCurie = uiEntity.getTaxon().getCurie();
        if (StringUtils.isEmpty(taxonCurie)) {
            addMessageResponse("taxon", requiredMessage);
            return null;
        }
        ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(taxonCurie);
        if (taxon.getEntity() == null) {
            addMessageResponse("taxon", invalidMessage);
            return null;
        }
        return taxon.getEntity();
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
