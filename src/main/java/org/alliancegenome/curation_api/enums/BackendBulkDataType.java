package org.alliancegenome.curation_api.enums;

public enum BackendBulkDataType {
    RGD("NCBITaxon:10116"), 
    MGI("NCBITaxon:10090"), 
    SGD("NCBITaxon:559292"), 
    HUMAN("NCBITaxon:9606"), 
    ZFIN("NCBITaxon:7955"), 
    FB("NCBITaxon:7227"), 
    WB("NCBITaxon:6239")
    ;
    
    public final String taxonId;
    
    private BackendBulkDataType(String taxonId) {
        this.taxonId = taxonId;
    }
    
    public String getTaxonId() {
        return this.taxonId;
    }
}