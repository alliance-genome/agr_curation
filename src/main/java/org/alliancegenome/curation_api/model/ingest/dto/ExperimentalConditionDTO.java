package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.Data;

@Data
public class ExperimentalConditionDTO {

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_class")
    private String conditionClass;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_statement")
    private String conditionStatement;
    

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_id")
    private String conditionId;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_quantity")
    private String conditionQuantity;
    

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_gene_ontology")
    private String conditionGeneOntology;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_anatomy")
    private String conditionAnatomy;
    
    @JsonProperty("condition_taxon")
    private String conditionTaxon;
    

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_chemical")
    private String conditionChemical;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_free_text")
    private String conditionFreeText;
    
    @JsonView({View.FieldsOnly.class})
    private Boolean internal = false;
}
