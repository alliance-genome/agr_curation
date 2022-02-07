package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExperimentalConditionFmsDTO extends BaseDTO {

    private String conditionClassId;
    private String conditionStatement;
    private String conditionId;
    private String conditionQuantity;
    private String anatomicalOntologyId;
    private String geneOntologyId;
    @JsonProperty("NCBITaxonId")
    private String ncbiTaxonId;
    private String chemicalOntologyId;

}
