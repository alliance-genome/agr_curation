package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class DiseaseAnnotationDTO {

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("mod_id")
    private String modId;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("unique_id")
    private String uniqueID;

    @JsonView({View.FieldsOnly.class})
    private String subject;

    @JsonView({View.FieldsOnly.class})
    private String object;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("data_provider")
    private List<String> dataProvider;

    @JsonView({View.FieldsOnly.class})
    private Boolean negated = false;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("predicate")
    private String diseaseRelation;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("genetic_sex")
    private String geneticSex;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("modified_by")
    private String modifiedBy;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("created_by")
    private String createdBy;
    
    @JsonView({View.FieldsAndLists.class})
    @JsonProperty("evidence_codes")
    private List<String> evidenceCodes;

    @JsonView({View.FieldsAndLists.class})
    private List<ConditionRelationDTO> conditionRelations;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("disease_genetic_modifier")
    private String geneticModifier;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("disease_genetic_modifier_relation")
    @Enumerated(EnumType.STRING)
    private GeneticModifierRelation geneticModifierRelation;

    @JsonView({View.FieldsAndLists.class})
    private List<String> with;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("single_reference")
    private String singleReference;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("disease_annotation_summary")
    private String diseaseAnnotationSummary;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("disease_annotation_note")
    private String diseaseAnnotationNote;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("table_id")
    protected Long tableId;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("creation_date")
    private String creationDate;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("date_last_modified")
    private String lastUpdated;

    public enum GeneticModifierRelation {
        ameliorated_by,
        exacerbated_by,
    }

}
