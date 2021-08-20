package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.*;

import org.alliancegenome.curation_api.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DiseaseModelAnnotationDTO extends BaseDTO {

    private String objectId;
    private String objectName;
    
    private DiseaseObjectRelationDTO objectRelation;

    private Negation negation;
    
    private List<String> primaryGeneticEntityIDs;
    
    @JsonProperty("DOid")
    private String doId;
    private List<DataProviderDTO> dataProvider;
    
    private List<String> with;
    private EvidenceDTO evidence;

    private Date dateAssigned;
    private List<ConditionRelationDTO> conditionRelations;
    

    private enum Negation {
        not;
    }

}
