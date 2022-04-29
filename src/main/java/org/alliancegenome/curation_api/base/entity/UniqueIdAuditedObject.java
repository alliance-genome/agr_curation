package org.alliancegenome.curation_api.base.entity;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
public class UniqueIdAuditedObject extends GeneratedAuditedObject{

    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "uniqueId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @Column(unique = true, length = 2000)
    @JsonView({View.FieldsOnly.class})
    @EqualsAndHashCode.Include
    private String uniqueId;

}
