package org.alliancegenome.curation_api.model.ingest.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class ExperimentalConditionDTO extends BaseDTO {

    private String conditionClassId;
    private String conditionStatement;
    private String conditionId;
    private String conditionQuantity;
    private String anatomicalOntologyId;
    private String geneOntologyId;

    @JsonProperty("NCBITaxonId")
    private String ncbitaxonId;
    private String chemicalOntologyId;

}
