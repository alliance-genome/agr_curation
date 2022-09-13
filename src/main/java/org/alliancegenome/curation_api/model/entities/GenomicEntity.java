package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = AffectedGenomicModel.class, name = "AffectedGenomicModel"),
		@JsonSubTypes.Type(value = Allele.class, name = "Allele"),
		@JsonSubTypes.Type(value = Gene.class, name = "Gene")
})
@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"synonyms", "crossReferences", "secondaryIdentifiers"}, callSuper = true)
@AGRCurationSchemaVersion("1.2.4")
public class GenomicEntity extends BiologicalEntity {

	//@Analyzer(definition = "caseInsensitiveAnalyzer")
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition="TEXT")
	@JsonView({View.FieldsOnly.class})
	private String name;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = @Index( columnList = "genomicentities_curie"))
	@JsonView({View.FieldsAndLists.class})
	private List<Synonym> synonyms;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = @Index( columnList = "genomicentity_curie"))
	@JsonView({View.FieldsAndLists.class})
	private List<CrossReference> crossReferences;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@ElementCollection
	@JoinTable(indexes = @Index( columnList = "genomicentity_curie"))
	@JsonView({View.FieldsAndLists.class})
	private List<String> secondaryIdentifiers;

}
