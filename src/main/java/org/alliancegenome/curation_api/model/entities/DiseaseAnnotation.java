package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import lombok.*;



@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "type")
@JsonSubTypes({ 
  @Type(value = AGMDiseaseAnnotation.class, name = "AGMDiseaseAnnotation"), 
  @Type(value = AlleleDiseaseAnnotation.class, name = "AlleleDiseaseAnnotation"), 
  @Type(value = GeneDiseaseAnnotation.class, name = "GeneDiseaseAnnotation") 
})
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
//@ToString(exclude = {"genomicLocations"})
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
public class DiseaseAnnotation extends Association {
    
    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "modId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @Column(unique = true)
    @JsonView({View.FieldsOnly.class})
    @EqualsAndHashCode.Include
    private String modId;
    
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private DOTerm object;
    
    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
    @KeywordField(name = "negated_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
    @JsonView({View.FieldsOnly.class})
    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean negated = false;

    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "diseaseRelation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private DiseaseRelation diseaseRelation;

    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToMany
    @JsonView({View.FieldsAndLists.class})
    private List<EcoTerm> evidenceCodes;
    
    
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToMany
    @JoinTable(indexes = @Index( columnList = "diseaseannotation_id"))
    @JsonView({View.FieldsAndLists.class})
    private List<Gene> with;
    
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private Reference reference;


    public enum DiseaseRelation {
        is_model_of,
        is_implicated_in,
        is_marker_for;
    }

}

