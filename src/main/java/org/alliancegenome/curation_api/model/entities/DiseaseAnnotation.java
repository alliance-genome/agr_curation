package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = AGMDiseaseAnnotation.class, name = "AGMDiseaseAnnotation"),
	@Type(value = AlleleDiseaseAnnotation.class, name = "AlleleDiseaseAnnotation"),
	@Type(value = GeneDiseaseAnnotation.class, name = "GeneDiseaseAnnotation")
})
@MappedSuperclass
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Annotation.class })
@Schema(name = "Disease_Annotation", description = "Annotation class representing a disease annotation")
public abstract class DiseaseAnnotation extends Annotation {

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private DOTerm diseaseAnnotationObject;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "negated_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean negated = false;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm relation;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "abbreviation",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "abbreviation_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "association_id"),
		inverseJoinColumns = @JoinColumn(name = "evidencecodes_id"),
		indexes = {
			@Index(columnList = "association_id"),
			@Index(columnList = "evidencecodes_id")
		}
	)
	private List<ECOTerm> evidenceCodes;

	@IndexedEmbedded(includePaths = {
			"curie", "modEntityId", "modInternalId", "curie_keyword", "modEntityId_keyword", "modInternalId_keyword",
			"geneSymbol.formatText", "geneSymbol.displayText", "geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword",
			"geneFullName.formatText", "geneFullName.displayText", "geneFullName.formatText_keyword", "geneFullName.displayText_keyword",
			"geneSystematicName.formatText", "geneSystematicName.displayText", "geneSystematicName.formatText_keyword", "geneSystematicName.displayText_keyword",
			"geneSynonyms.formatText", "geneSynonyms.displayText", "geneSynonyms.formatText_keyword", "geneSynonyms.displayText_keyword",
			"geneSecondaryIds.secondaryId", "geneSecondaryIds.secondaryId_keyword", "name", "name_keyword", "symbol", "symbol_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "association_id"),
		inverseJoinColumns = @JoinColumn(name = "with_id"),
		indexes = {
			@Index(columnList = "association_id"),
			@Index(columnList = "with_id")
		}
	)
	private List<Gene> with;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm annotationType;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "association_id"),
		inverseJoinColumns = @JoinColumn(name = "diseasequalifiers_id"),
		indexes = {
			@Index(columnList = "association_id"),
			@Index(columnList = "diseasequalifiers_id")
		}
	)
	private List<VocabularyTerm> diseaseQualifiers;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm geneticSex;

	@IndexedEmbedded(includePaths = {"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
			"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private DataProvider secondaryDataProvider;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "association_id"),
		inverseJoinColumns = @JoinColumn(name = "diseasegeneticmodifiers_id"),
		indexes = {
			@Index(columnList = "association_id"),
			@Index(columnList = "diseasegeneticmodifiers_id")
		}
	)
	private List<BiologicalEntity> diseaseGeneticModifiers;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
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
	public abstract String getSubjectIdentifier();

	@Transient
	@JsonIgnore
	public String getDataProviderString() {
		StringBuilder builder = new StringBuilder(dataProvider.getSourceOrganization().getAbbreviation());
		if (secondaryDataProvider != null) {
			builder.append(" via ");
			builder.append(secondaryDataProvider.getSourceOrganization().getAbbreviation());
		}
		return builder.toString();
	}
}
