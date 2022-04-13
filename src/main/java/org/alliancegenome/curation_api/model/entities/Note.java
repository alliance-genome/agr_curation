package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.BaseGeneratedEntity;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name="Note", description="POJO that represents the Note")
public class Note extends BaseGeneratedEntity {
    
    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "freeText_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @JsonView({View.FieldsOnly.class})
    @Column(columnDefinition="TEXT")
    private String freeText;
    
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private VocabularyTerm noteType;
    
    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
    @KeywordField(name = "internal_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
    @JsonView({View.FieldsOnly.class})
    @Column(columnDefinition = "boolean default true", nullable = false)
    private Boolean internal = true;
    
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonView({View.FieldsAndLists.class})
    private List<Reference> references;
}
