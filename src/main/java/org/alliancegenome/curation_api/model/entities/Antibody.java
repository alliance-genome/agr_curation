package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.interfaces.CurieSubdomain;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "antibody", description = "Antibody: an immunoglobulin reagent used for detection")
@ToString(exclude = { "antibodyTargetGenes", "references", "crossReferences" }, callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { Reagent.class })
@Table(indexes = {
	@Index(name = "antibody_clonality_index", columnList = "clonality_id"),
	@Index(name = "antibody_heavychainisotype_index", columnList = "heavychainisotype_id"),
	@Index(name = "antibody_lightchainisotype_index", columnList = "lightchainisotype_id"),
	@Index(name = "antibody_antigentaxon_index", columnList = "antigentaxon_id"),
	@Index(name = "antibody_antigentaxonterm_index", columnList = "antigentaxonterm_id"),
	@Index(name = "antibody_hosttaxonterm_index", columnList = "hosttaxonterm_id"),
	@Index(name = "antibody_originalreference_index", columnList = "originalreference_id")
})
@CurieSubdomain(MatiSubdomain.ANTIBODY)
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

	// Antigen source species is an open set (potentially any organism), unlike host taxon's small
	// closed list -- so it stays NCBITaxonTerm-backed like before, not folded into the antibody_taxon-
	// style single-CV approach. antigenTaxonTerm below is a separate, disjoint, closed CV that only
	// ever holds non-taxonomic values (not specified, other, etc.) explaining why antigenTaxon is
	// absent -- it must never contain an NCBITaxon curie, or a species query against antigenTaxon
	// alone would silently miss records, since it wouldn't know to also check antigenTaxonTerm.
	@IndexedEmbedded(includePaths = { "name", "curie", "name_keyword", "curie_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private NCBITaxonTerm antigenTaxon;

	@IndexedEmbedded(includePaths = { "name", "definition", "name_keyword", "definition_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm antigenTaxonTerm;

	@IndexedEmbedded(includePaths = { "name", "definition", "name_keyword", "definition_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ CurationView.FieldsOnly.class })
	private VocabularyTerm hostTaxonTerm;

	@IndexedEmbedded(includePaths = {
		"curie", "primaryExternalId", "modInternalId",
		"curie_keyword", "primaryExternalId_keyword", "modInternalId_keyword",
		"geneSymbol.formatText", "geneSymbol.displayText",
		"geneSymbol.formatText_keyword", "geneSymbol.displayText_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonIgnoreProperties({
		"geneGenomicLocationAssociations",
		"alleleGeneAssociations",
		"sequenceTargetingReagentGeneAssociations",
		"transcriptGeneAssociations",
		"constructGenomicEntityAssociations"
	})
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
