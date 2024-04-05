package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = AGMPhenotypeAnnotation.class, name = "AGMPhenotypeAnnotation"), @Type(value = AllelePhenotypeAnnotation.class, name = "AllelePhenotypeAnnotation"),
	@Type(value = GenePhenotypeAnnotation.class, name = "GenePhenotypeAnnotation") })
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Annotation.class })
@Schema(name = "Phenotype_Annotation", description = "Annotation class representing a phenotype annotation")
@Table(indexes = { 
	@Index(name = "phenotypeannotation_relation_index", columnList = "relation_id")
})
public abstract class PhenotypeAnnotation extends Annotation {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "phenotypeAnnotationObject_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private String phenotypeAnnotationObject;

	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.PhenotypeAnnotationView.class, View.ForPublic.class })
	@JoinTable(indexes = {
		@Index(name = "phenotypeannotation_ontologyterm_phenotypeannotation_index", columnList = "phenotypeannotation_id"),
		@Index(name = "phenotypeannotation_ontologyterm_phenotypeterms_index", columnList = "phenotypeterms_id")
	})
	private List<PhenotypeTerm> phenotypeTerms;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm relation;
	
	@IndexedEmbedded(includePaths = {"referencedCurie", "displayName", "referencedCurie_keyword", "displayName_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval=true)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private CrossReference crossReference;
	
	public abstract String getSubjectCurie();

	public abstract String getSubjectTaxonCurie();

	public abstract String getSubjectSpeciesName();
	
	public abstract String getSubjectIdentifier();

	@Transient
	@JsonIgnore
	public String getDataProviderString() {
		return dataProvider.getSourceOrganization().getAbbreviation();
	}
}
