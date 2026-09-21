package org.alliancegenome.curation_api.constants;

import java.util.List;

/**
 * SCRUM-6152 - SGD strain-level taxa that must resolve to the single curated
 * Saccharomyces cerevisiae species row when building public search documents.
 *
 * Only 7 of the 13 SGD AGMs carry the canonical taxon NCBITaxon:559292; the rest sit on
 * strain-level taxa that have no row in the `species` table, so the species join in the
 * search DAOs yields a null species and those models never appear under the
 * "Saccharomyces cerevisiae" facet on the public site.
 *
 * NCBITaxon:4932 is the conceptual root for the yeast strains per SCRUM-6152, but the
 * curated `species` row stays pinned to NCBITaxon:559292: repointing it would move the
 * species anchor out from under the ~8k SGD genes and ~23k SGD alleles already on 559292.
 * The grouping is therefore applied at query time only - no `biologicalentity.taxon_id` is
 * changed and no new entity is associated with NCBITaxon:4932.
 *
 * This is deliberately hardcoded: the NCBITaxon closure is not loaded into
 * `ontologytermclosure` (only DO/GO are), so there is no generic ancestor lookup available.
 */
public final class YeastStrainTaxonConstants {

	private YeastStrainTaxonConstants() {
		// Hidden from view, as it is a utility class
	}

	/** The taxon the curated Saccharomyces cerevisiae `species` row is keyed to. */
	public static final String CANONICAL_SCE_TAXON = "NCBITaxon:559292";

	/** Yeast taxa that carry no `species` row of their own and must fall back to the canonical one. */
	public static final List<String> SCE_STRAIN_TAXA = List.of(
		"NCBITaxon:4932",   // Saccharomyces cerevisiae (root term)
		"NCBITaxon:285006", // Saccharomyces cerevisiae RM11-1a
		"NCBITaxon:580239", // Saccharomyces cerevisiae SK1
		"NCBITaxon:580240", // Saccharomyces cerevisiae W303
		"NCBITaxon:658763", // Saccharomyces cerevisiae Sigma1278b
		"NCBITaxon:947036"  // Saccharomyces cerevisiae FL100
	);
}
