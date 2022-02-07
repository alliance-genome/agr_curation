package org.alliancegenome.curation_api.model.ingest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.view.View;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class DiseaseAnnotationDTO {

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("mod_id")
    private String modId;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("unqiue_id")
    private String uniqueID;

    @JsonView({View.FieldsOnly.class})
    private String subject;

    @JsonView({View.FieldsOnly.class})
    private String object;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("data_provider")
    private String dataProvider;

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

    @JsonView({View.FieldsAndLists.class})
    @JsonProperty("evidence_codes")
    private List<String> evidenceCodes;

    @JsonView({View.FieldsAndLists.class})
    private List<ConditionRelation> conditionRelations;

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
    private String reference;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("disease_annotation_summary")
    private String diseaseAnnotationSummary;

    @JsonView({View.FieldsOnly.class})
    protected Long id;

    @JsonView({View.FieldsOnly.class})
    private LocalDateTime created;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("date_last_modified")
    private String lastUpdated;

    public enum GeneticModifierRelation {
        ameliorated_by,
        exacerbated_by,
    }

}
