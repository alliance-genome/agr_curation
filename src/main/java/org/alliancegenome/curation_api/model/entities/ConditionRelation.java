package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.base.entity.BaseGeneratedAndUniqueIdEntity;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import java.util.List;

@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(name = "Disease_Relation", description = "Condition class ")
public class ConditionRelation extends BaseGeneratedAndUniqueIdEntity  {

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("condition_relation_type")
    private String relationType;

    @JsonView({View.FieldsAndLists.class})
    private List<Condition> conditions;

    @JsonView({View.FieldsOnly.class})
    private List<PaperHandle> paperHandles;


}
