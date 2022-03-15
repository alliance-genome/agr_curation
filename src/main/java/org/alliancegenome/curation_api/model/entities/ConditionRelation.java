package org.alliancegenome.curation_api.model.entities;

import java.util.*;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.BaseGeneratedAndUniqueIdEntity;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "ConditionRelation", description = "POJO that describes the Condition Relation")
public class ConditionRelation extends BaseGeneratedAndUniqueIdEntity {

    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private VocabularyTerm conditionRelationType;

    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToMany
    @JsonView({View.FieldsAndLists.class})
    private List<ExperimentalCondition> conditions;


    public void addExperimentCondition(ExperimentalCondition experimentalCondition) {
        if (conditions == null)
            conditions = new ArrayList<>();
        conditions.add(experimentalCondition);
    }
}
