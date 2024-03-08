package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.associations.geneAssociations.GeneGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Annotation.class })
@Schema(name = "Gene_Interaction", description = "Annotation class representing a gene interaction")
@Table(indexes = { 
	@Index(name = "geneinteraction_curie_index", columnList = "curie"),
	@Index(name = "geneinteraction_interactionsource_index", columnList = "interactionsource_id"),
	@Index(name = "geneinteraction_interactiontype_index", columnList = "interactiontype_id"),
	@Index(name = "geneinteraction_interactorarole_index", columnList = "interactorarole_id"),
	@Index(name = "geneinteraction_interactorbrole_index", columnList = "interactorbrole_id"),
	@Index(name = "geneinteraction_interactoratype_index", columnList = "interactoratype_id"),
	@Index(name = "geneinteraction_interactorbtype_index", columnList = "interactorbtype_id"),
})

public abstract class GeneInteraction extends GeneGeneAssociation {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "curie_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	protected String curie;
	
	@IndexedEmbedded(includePaths = {"referencedCurie", "displayName", "referencedCurie_keyword", "displayName_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
	@JoinTable(indexes = { 
		@Index(columnList = "geneinteraction_id, crossreferences_id", name = "geneinteraction_crossreference_gi_xref_index"),
		@Index(columnList = "geneinteraction_id", name = "geneinteraction_crossreference_geneinteraction_index"),
		@Index(columnList = "crossreferences_id", name = "geneinteraction_crossreference_crossreferences_index")
	})
	@JsonView({ View.FieldsAndLists.class, View.GeneInteractionView.class })
	private List<CrossReference> crossReferences;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm interactionSource;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm interactionType;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm interactorARole;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm interactorBRole;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm interactorAType;
	
	@IndexedEmbedded(includePaths = {"curie", "name", "secondaryIdentifiers", "synonyms.name", "namespace",
			"curie_keyword", "name_keyword", "secondaryIdentifiers_keyword", "synonyms.name_keyword", "namespace_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private MITerm interactorBType;
}
