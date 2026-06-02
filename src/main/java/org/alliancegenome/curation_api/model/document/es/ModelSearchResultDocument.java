package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.Set;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 * SCRUM-5124 - public ES search-result document for Affected Genomic Models.
 *
 * Mirrors the field set produced by the legacy Neo4j ModelTranslator (now sourced from
 * the curation Postgres store). Field set audited from ModelTranslator + ModelDocumentCache
 * (populated by ModelIndexerRepository).
 *
 * The simple direct-lookup fields and the basic name/id/species set are declared here.
 * Multi-hop fields (alleles, genes, diseases*, phenotypeStatements) are also declared as
 * collections so the future ModelSearchResultDocumentBuilder can populate them from
 * AffectedGenomicModel.components, agmDiseaseAnnotations, agmPhenotypeAnnotations, and
 * the OntologyTermClosure tooling added in SCRUM-5126.
 */
@Data
@JsonView(CurationView.ModelSearchResultDocument.class)
public class ModelSearchResultDocument extends ESDocument {

	{
		category = "model_search_result";
		searchable = true;
	}

	// --- IDs / URLs (direct getters on AffectedGenomicModel / parents) ---
	private String primaryKey;          // SubmittedObject.primaryExternalId
	private String globalId;            // SubmittedObject.primaryExternalId (matches primaryKey in practice)
	private String localId;             // SubmittedObject.modInternalId
	private String modCrossRefCompleteUrl; // built from CrossReference + ResourceDescriptorPage.urlTemplate

	// --- Names (from AgmFullNameSlotAnnotation + species composition) ---
	private String name;                // agmFullName.displayText
	private String nameText;            // agmFullName.formatText
	private String nameKey;             // agmFullName.formatText + " (" + speciesAbbrev(taxon) + ")"

	// --- Species (single value) ---
	private String species;             // BiologicalEntity.taxon.species.name

	// --- Slot-annotation collections ---
	private Set<String> secondaryIds = new HashSet<>(); // agmSecondaryIds[*].secondaryId
	private Set<String> synonyms = new HashSet<>();     // agmSynonyms[*].displayText

	// --- Aggregated phenotype set ---
	private Set<String> phenotypeStatements = new HashSet<>(); // agmPhenotypeAnnotations[*].phenotypeAnnotationObject

	// --- Aggregated disease sets ---
	private Set<String> diseases = new HashSet<>();              // agmDiseaseAnnotations[*].diseaseAnnotationObject.name
	private Set<String> diseasesWithParents = new HashSet<>();   // diseases plus DOTerm ancestor names via OntologyTermClosure
	private Set<String> diseasesAgrSlim = new HashSet<>();       // diseases mapped through Alliance disease slim subset

	// --- Multi-hop allele/gene aggregates (formatted with species suffix, e.g. "T<2J> (Mmu)") ---
	private Set<String> alleles = new HashSet<>(); // components[*].agmAlleleAssociationObject.alleleSymbol.formatText + species suffix
	private Set<String> genes = new HashSet<>();   // components[*].agmAlleleAssociationObject.alleleGeneAssociations[*].alleleGeneAssociationObject.geneSymbol.formatText + species suffix

	// --- Sort/relevance hint (legacy doc always emits 0 by default) ---
	private Double popularity = 0D;
}