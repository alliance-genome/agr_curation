package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "HighThroughputExpressionDatasetAnnotation", description = "POJO that represents the HighThroughputExpressionDatasetAnnotation")
public class HighThroughputExpressionDatasetAnnotation extends SubmittedObject {

	@JsonView({ View.FieldsOnly.class })
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	private String name;

	@IndexedEmbedded(includePaths = {"curie", "primaryCrossReferenceCurie", "crossReferences.referencedCurie", "curie_keyword", "primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "htpdatasetannotation_reference_htpdataset_index", columnList = "highthroughputexpressiondatasetannotation_id"),
		@Index(name = "htpdatasetannotation_reference_references_index", columnList = "references_id")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<Reference> references;

	@IndexedEmbedded(includePaths = { "freeText", "noteType.name", "references.curie", "references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword", "references.primaryCrossReferenceCurie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsOnly.class })
	private Note relatedNote;

	@JsonView({ View.FieldsOnly.class })
	@GenericField(projectable = Projectable.YES, sortable = Sortable.YES)
	private Integer numberOfChannels;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "highthroughputexpressiondatasetannotation_categorytags", indexes = { @Index(name = "htpdatasetannotation_htpdatasetid_index", columnList = "highthroughputexpressiondatasetannotation_id"), @Index(name = "htpdatasetannotation_categorytags_index", columnList = "categorytags_id")})
	List<VocabularyTerm> categoryTags;

}
