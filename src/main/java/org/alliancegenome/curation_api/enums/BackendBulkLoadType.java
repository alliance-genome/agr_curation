package org.alliancegenome.curation_api.enums;

public enum BackendBulkLoadType {
	DISEASE_ANNOTATION("json"),
	GENE("json"),
	ALLELE("json"),
	AGM("json"),
	CONSTRUCT("json"),
	AGM_DISEASE_ANNOTATION("json"),
	ALLELE_DISEASE_ANNOTATION("json"),
	GENE_DISEASE_ANNOTATION("json"),
	ONTOLOGY("owl"),
	PHENOTYPE("json"),
	MOLECULE("json"),
	FULL_INGEST("json"),
	RESOURCE_DESCRIPTOR("yaml"),
	ORTHOLOGY("json"),
	ALLELE_ASSOCIATION("json"),
	CONSTRUCT_ASSOCIATION("json"),
	VARIANT("json"),

	// GFF all from the same file but split out
	GFF("gff"), // For Database entries
	
	GFF_EXON("gff"),
	GFF_CDS("gff"),
	GFF_TRANSCRIPT("gff"),
	
	INTERACTION_MOL("tsv"),
	INTERACTION_GEN("tsv"),
	PARALOGY("json"),
	SEQUENCE_TARGETING_REAGENT("json"),
	EXPRESSION("json"),
	HTPDATASET("json"),
	;

	public String fileExtension;

	private BackendBulkLoadType(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
