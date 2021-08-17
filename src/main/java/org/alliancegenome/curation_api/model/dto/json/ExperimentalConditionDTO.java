package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
