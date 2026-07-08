package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.view.CurationView;
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
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "antibody", description = "Antibody: an immunoglobulin reagent used for detection")
@ToString(exclude = { "antibodyTargetGenes", "references", "crossReferences" }, callSuper = true)
@AGRCurationSchemaVersion(min = "2.11.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Reagent.class })
public class Antibody extends Reagent {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition = "TEXT")
	@JsonView({ CurationView.FieldsOnly.class })
	private String name;

	@IndexedEmbedded(includePaths = { "name", "name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm clonality;

	@IndexedEmbedded(includePaths = { "name", "name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm heavyChainIsotype;

	@IndexedEmbedded(includePaths = { "name", "name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm lightChainIsotype;

	@IndexedEmbedded(includePaths = { "name", "curie", "name_keyword", "curie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private NCBITaxonTerm antigenTaxon;

	@IndexedEmbedded(includePaths = { "name", "curie", "name_keyword", "curie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private NCBITaxonTerm taxon;

	@IndexedEmbedded(includePaths = { "primaryExternalId", "modInternalId", "symbol",
		"primaryExternalId_keyword", "modInternalId_keyword", "symbol_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class })
	@JoinTable(indexes = {
		@Index(name = "antibody_gene_antibody_index", columnList = "antibody_id"),
		@Index(name = "antibody_gene_gene_index", columnList = "antibodytargetgenes_id")
	})
	private List<Gene> antibodyTargetGenes;

	@IndexedEmbedded(includePaths = { "primaryCrossReferenceCurie", "crossReferences.referencedCurie", "curie",
		"primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword", "curie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class })
	@JoinTable(indexes = {
		@Index(name = "antibody_reference_antibody_index", columnList = "antibody_id"),
		@Index(name = "antibody_reference_references_index", columnList = "references_id")
	})
	private List<Reference> references;

	@IndexedEmbedded(includePaths = { "curie", "primaryCrossReferenceCurie", "curie_keyword", "primaryCrossReferenceCurie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private Reference originalReference;

	@IndexedEmbedded(includePaths = { "referencedCurie", "displayName", "referencedCurie_keyword", "displayName_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class })
	@JoinTable(indexes = {
		@Index(name = "antibody_crossreference_antibody_index", columnList = "antibody_id"),
		@Index(name = "antibody_crossreference_crossreferences_index", columnList = "crossreferences_id")
	})
	private List<CrossReference> crossReferences;
}
