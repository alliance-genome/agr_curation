package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.List;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"vocabulary", "vocabularyTermSets"}, callSuper = true)
@Table(indexes = {
	@Index(name = "vocabularyterm_name_index", columnList = "name"),
	@Index(name = "vocabularyterm_vocabulary_id_index", columnList = "vocabulary_id"),
	@Index(name = "vocabularyterm_createdby_index", columnList = "createdBy_id"),
	@Index(name = "vocabularyterm_updatedby_index", columnList = "updatedBy_id")
})
@Schema(name = "VocabularyTerm", description = "POJO that represents the Vocabulary Term")
@AGRCurationSchemaVersion(min = "1.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {AuditedObject.class})
public class VocabularyTerm extends AuditedObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.GeneToGeneOrthologyDocument.class, View.ModelDocumentView.class})
	private String name;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "abbreviation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.GeneToGeneOrthologyDocument.class})
	private String abbreviation;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "definition_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	@Column(columnDefinition = "TEXT")
	private String definition;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.VocabularyTermView.class, View.VocabularyTermUpdate.class, View.VocabularyTermSetView.class})
	private Vocabulary vocabulary;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "synonyms_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView({View.FieldsAndLists.class, View.VocabularyTermView.class})
	@JoinTable(indexes = @Index(columnList = "vocabularyterm_id"))
	@Column(columnDefinition = "TEXT")
	private List<String> synonyms;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany(mappedBy = "memberTerms")
	@JsonView({View.VocabularyTermView.class, View.VocabularyTermUpdate.class})
	private List<VocabularyTermSet> vocabularyTermSets;
}
