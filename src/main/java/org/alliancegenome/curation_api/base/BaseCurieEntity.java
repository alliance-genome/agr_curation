package org.alliancegenome.curation_api.base;

import java.time.LocalDateTime;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@MappedSuperclass
public class BaseCurieEntity extends BaseEntity {

    @Id @DocumentId
    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView({View.FieldsOnly.class})
    @EqualsAndHashCode.Include
    private String curie;

    @GenericField
    @CreationTimestamp
    @JsonView({View.FieldsOnly.class})
    private LocalDateTime created;

    @GenericField
    @UpdateTimestamp
    @JsonView({View.FieldsOnly.class})
    private LocalDateTime lastUpdated;

}
