package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = AGMDiseaseAnnotation.class, name = "AGMDiseaseAnnotation"), @Type(value = AlleleDiseaseAnnotation.class, name = "AlleleDiseaseAnnotation"),
	@Type(value = GeneDiseaseAnnotation.class, name = "GeneDiseaseAnnotation") })
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ConditionRelation.class, Note.class, SingleReferenceAssociation.class })
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")

@Table(indexes = { 
	@Index(name = "DiseaseAnnotation_object_index", columnList = "object_curie"),
	@Index(name = "DiseaseAnnotation_relation_index", columnList = "relation_id"),
	@Index(name = "DiseaseAnnotation_annotationType_index", columnList = "annotationType_id"),
	@Index(name = "DiseaseAnnotation_geneticSex_index", columnList = "geneticSex_id"),
	@Index(name = "DiseaseAnnotation_secondaryDataProvider_index", columnList = "secondaryDataProvider_id"),
	@Index(name = "DiseaseAnnotation_diseaseGeneticModifierRelation_index", columnList = "diseaseGeneticModifierRelation_id"),
})

public abstract class DiseaseAnnotation extends Annotation {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private DOTerm object;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "negated_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean negated = false;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm relation;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(indexes = { @Index(columnList = "diseaseannotation_id"), @Index(columnList = "evidencecodes_curie")})
	private List<ECOTerm> evidenceCodes;

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SELECT)
	@JoinTable(indexes = { @Index(columnList = "diseaseannotation_id"), @Index(columnList = "with_curie") })
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	private List<Gene> with;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm annotationType;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(indexes = { @Index(columnList = "diseaseannotation_id"), @Index(columnList = "diseasequalifiers_id")})
	private List<VocabularyTerm> diseaseQualifiers;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm geneticSex;

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private DataProvider secondaryDataProvider;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(indexes = { @Index(columnList = "diseaseannotation_id"), @Index(columnList = "diseasegeneticmodifiers_curie")})
	private List<BiologicalEntity> diseaseGeneticModifiers;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm diseaseGeneticModifierRelation;

	@Transient
	public abstract String getSubjectCurie();

	@Transient
	public abstract String getSubjectTaxonCurie();

	@Transient
	public abstract String getSubjectSpeciesName();

	@Transient
	@JsonIgnore
	public String getDataProviderString(){
		StringBuilder builder = new StringBuilder(dataProvider.getSourceOrganization().getAbbreviation());
		if(secondaryDataProvider != null){
			builder.append(" via ");
			builder.append(secondaryDataProvider.getSourceOrganization().getAbbreviation());
		}
		return builder.toString();
	}
}
