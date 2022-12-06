package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"synonyms", "crossReferences", "secondaryIdentifiers"}, callSuper = true)
@AGRCurationSchemaVersion(min="1.2.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={BiologicalEntity.class})
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
	@JoinTable(indexes = @Index( columnList = "genomicentities_curie, synonyms_id"))
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({View.FieldsAndLists.class})
	private List<Synonym> synonyms;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = {
		@Index(columnList = "genomicentity_curie, crossreferences_curie"),
		@Index(columnList = "genomicentity_curie", name = "genomicentity_crossreference_genomicentity_curie_index"),
		@Index(columnList = "crossreferences_curie", name = "genomicentity_crossreference_crossreferences_curie_index")
	})
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JsonView({View.FieldsAndLists.class})
	private List<CrossReference> crossReferences;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@ElementCollection
	@JoinTable(indexes = @Index(columnList = "genomicentity_curie"))
	@JsonView({View.FieldsAndLists.class})
	private List<String> secondaryIdentifiers;

}
