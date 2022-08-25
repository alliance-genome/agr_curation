package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;

import org.alliancegenome.curation_api.model.bridges.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.*;
import org.hibernate.search.mapper.pojo.common.annotation.Param;
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
@Inheritance(strategy = InheritanceType.JOINED)
//@ToString(exclude = {"genomicLocations"})
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
public abstract class DiseaseAnnotation extends Association {
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modEntityId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(unique = true)
	@JsonView({View.FieldsOnly.class})
	@EqualsAndHashCode.Include
	private String modEntityId;

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

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private VocabularyTerm diseaseRelation;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotationUpdate.class})
	private List<EcoTerm> evidenceCodes;
	
	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotationUpdate.class})
	private List<ConditionRelation> conditionRelations;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = @Index( columnList = "diseaseannotation_id"))
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotationUpdate.class})
	private List<Gene> with;
	
	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private Reference singleReference;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private VocabularyTerm annotationType;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotationUpdate.class})
	private List<VocabularyTerm> diseaseQualifiers;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private VocabularyTerm geneticSex;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany
	@JsonView({View.FieldsAndLists.class, View.DiseaseAnnotationUpdate.class})
	private List<Note> relatedNotes;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "dataProvider_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	private String dataProvider;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "secondaryDataProvider_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	private String secondaryDataProvider;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@PropertyBinding(binder = @PropertyBinderRef(type = BiologicalEntityPropertyBinder.class, params = @Param(name = "fieldName", value = "diseaseGeneticModifier"))) 
	@JsonView({View.FieldsOnly.class})
	//@JoinColumn(name = "diseasegeneticmodifier_curie", referencedColumnName = "curie")
	private BiologicalEntity diseaseGeneticModifier;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private VocabularyTerm diseaseGeneticModifierRelation;
	
	@Transient
	public abstract String getSubjectCurie();
	
	@Transient
	public abstract String getSubjectTaxonCurie();

}
