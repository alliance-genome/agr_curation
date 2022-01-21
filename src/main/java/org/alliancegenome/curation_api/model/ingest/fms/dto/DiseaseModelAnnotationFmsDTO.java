package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.*;

import org.alliancegenome.curation_api.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DiseaseModelAnnotationFmsDTO extends BaseDTO {

    private String objectId;
    
    private String objectName;
    
    private DiseaseObjectRelationFmsDTO objectRelation;

    private Negation negation;
    
    private List<String> primaryGeneticEntityIDs;
    
    @JsonProperty("DOid")
    private String doId;

    private List<DataProviderFmsDTO> dataProvider;
    
    private List<String> with;
    
    private EvidenceFmsDTO evidence;

    private Date dateAssigned;
    
    private List<ConditionRelationFmsDTO> conditionRelations;
    

    public enum Negation {
        not;
    }

}
