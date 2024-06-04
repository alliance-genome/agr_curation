package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "SequenceTargetingReagent", description = "POJO that represents the SequenceTargetingReagent")
// TODO: update min release after LinkML release 
@AGRCurationSchemaVersion(min = "2.3.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntity.class }, partial = true)
public class SequenceTargetingReagent extends GenomicEntity {

	@Column(columnDefinition = "TEXT")
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private String name;

    @IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.ConstructView.class })
	@JoinTable(indexes = {
		@Index(name = "sqtr_reference_sqtr_index", columnList = "sqtr_id"),
		@Index(name = "sqtr_reference_references_index", columnList = "references_id")
	})
    private List<Reference> references;

    @IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = @Index(columnList = "sqtr_id", name = "sqtr_synonym_sqtr_index"))
	@JsonView({ View.FieldsAndLists.class })
	private List<Synonym> synonyms;

    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "secondaryIdentifiers_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@ElementCollection
	@JsonView({View.FieldsAndLists.class, View.ConstructView.class})
	@JoinTable(indexes = @Index(name = "sqtr_secondaryidentifiers_sqtr_index", columnList = "sqtr_id"))
	private List<SecondaryIdSlotAnnotation> secondaryIdentifiers;

}
