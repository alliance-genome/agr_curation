package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.alliancegenome.curation_api.view.View;

@Setter
@Getter
public class Condition {

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_chemical")
    private ExperimentalCondition conditionChemical;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_quantity")
    private ExperimentalCondition conditionQuantity;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_anatomy")
    private ExperimentalCondition conditionAnatomy;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_gene_ontology")
    private ExperimentalCondition conditionGO;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_taxon")
    private ExperimentalCondition conditionTaxon;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_class")
    private ExperimentalCondition conditionClass;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_statement")
    private String conditionStatement;

}
