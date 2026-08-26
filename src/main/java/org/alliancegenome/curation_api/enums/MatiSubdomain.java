package org.alliancegenome.curation_api.enums;

/**
 * SCRUM-6358 — the MaTI subdomains AGRKB curies are minted from, and their three-digit codes.
 *
 * A minted curie is {@code AGRKB:<code><counter as %012d>}, e.g. {@code AGRKB:106000000000001} for
 * allele counter 1. The codes are assigned by MaTI itself (see its {@code subdomain} table, seeded
 * by {@code V0003__SCRUM-2024.sql}, {@code V0004__SCRUM-6210.sql} and {@code V0005__SCRUM-6358.sql});
 * this enum mirrors them so a caller cannot mint into the wrong subdomain by mistyping a string, and
 * so the registry is documented in one place on this side.
 *
 * A subdomain listed here is only mintable once its target class has somewhere to store a curie.
 * The four classes tracked by SCRUM-6463 (molecular and genetic interactions, HTP expression
 * datasets and samples) do not yet, so their entries are declared but unused.
 */
public enum MatiSubdomain {

	DISEASE_ANNOTATION("100", "disease_annotation"),
	REFERENCE("101", "reference"),
	RESOURCE("102", "resource"),
	PERSON("103", "person"),
	LABORATORY("104", "laboratory"),
	GENE("105", "gene"),
	ALLELE("106", "allele"),
	VARIANT("107", "variant"),
	AGM("108", "agm"),
	CONSTRUCT("109", "construct"),
	EXPRESSION_EXPERIMENT("110", "expression_experiment"),
	EXPRESSION_ANNOTATION("111", "expression_annotation"),
	SEQUENCE_TARGETING_REAGENT("112", "sequence_targeting_reagent"),
	ANTIBODY("113", "antibody"),
	HTP_EXPRESSION_DATASET("114", "htp_expression_dataset"),
	HTP_EXPRESSION_SAMPLE("115", "htp_expression_sample"),
	PHENOTYPE_ANNOTATION("116", "phenotype_annotation"),
	MOLECULAR_INTERACTION("117", "molecular_interaction"),
	GENETIC_INTERACTION("118", "genetic_interaction"),
	ASSEMBLY_COMPONENT("119", "assembly_component"),
	GENOME_ASSEMBLY("120", "genome_assembly");

	private final String code;
	private final String subdomainName;

	MatiSubdomain(String code, String subdomainName) {
		this.code = code;
		this.subdomainName = subdomainName;
	}

	/** The three-digit code that prefixes curies minted from this subdomain. */
	public String getCode() {
		return code;
	}

	/** The subdomain name MaTI is keyed by, as sent on the increment call. */
	public String getSubdomainName() {
		return subdomainName;
	}

	/** The prefix a curie from this subdomain starts with, e.g. {@code AGRKB:106}. */
	public String getCuriePrefix() {
		return "AGRKB:" + code;
	}
}
