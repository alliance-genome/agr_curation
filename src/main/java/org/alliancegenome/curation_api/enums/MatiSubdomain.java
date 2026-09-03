package org.alliancegenome.curation_api.enums;

/**
 * The MaTI subdomains AGRKB curies are minted from.
 *
 * Names only. Review feedback on #2860: the three-digit code that prefixes a minted curie is
 * returned by MaTI on the increment call and used by {@link
 * org.alliancegenome.curation_api.services.MatiService#mintCuries} from there, so mirroring it here
 * would be a second copy of MaTI's subdomain table — another list to maintain, and one that can
 * drift from the source of truth without anything failing.
 *
 * The name is what callers must supply and what cannot come from MaTI at compile time, so that is
 * what this enum is for: it stops a mistyped string minting into the wrong subdomain, or into none.
 * The codes are recorded in MaTI's own migrations (V0003__SCRUM-2024.sql, V0004__SCRUM-6210.sql,
 * V0005__SCRUM-6358.sql) if a human needs to look one up.
 *
 * A subdomain is only mintable once its target class has somewhere to store a curie. SCRUM-6463
 * added one to GeneGeneAssociation (both interaction classes) and to HTPExpressionDatasetAnnotation;
 * HTPExpressionDatasetSampleAnnotation still has none, so HTP_EXPRESSION_SAMPLE remains unusable.
 * Minting is not yet wired up for any of the three, so their entries are declared but unused.
 */
public enum MatiSubdomain {

	DISEASE_ANNOTATION("disease_annotation"),
	REFERENCE("reference"),
	RESOURCE("resource"),
	PERSON("person"),
	LABORATORY("laboratory"),
	GENE("gene"),
	ALLELE("allele"),
	VARIANT("variant"),
	AGM("agm"),
	CONSTRUCT("construct"),
	EXPRESSION_EXPERIMENT("expression_experiment"),
	EXPRESSION_ANNOTATION("expression_annotation"),
	SEQUENCE_TARGETING_REAGENT("sequence_targeting_reagent"),
	ANTIBODY("antibody"),
	HTP_EXPRESSION_DATASET("htp_expression_dataset"),
	HTP_EXPRESSION_SAMPLE("htp_expression_sample"),
	PHENOTYPE_ANNOTATION("phenotype_annotation"),
	MOLECULAR_INTERACTION("molecular_interaction"),
	GENETIC_INTERACTION("genetic_interaction"),
	ASSEMBLY_COMPONENT("assembly_component"),
	GENOME_ASSEMBLY("genome_assembly");

	private final String subdomainName;

	MatiSubdomain(String subdomainName) {
		this.subdomainName = subdomainName;
	}

	/** The subdomain name MaTI is keyed by, as sent on the increment call. */
	public String getSubdomainName() {
		return subdomainName;
	}
}
