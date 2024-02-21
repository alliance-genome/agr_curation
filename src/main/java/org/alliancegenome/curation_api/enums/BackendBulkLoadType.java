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
	PHENOTYPE_ANNOTATION("json"),
	MOLECULE("json"),
	FULL_INGEST("json"),
	RESOURCE_DESCRIPTOR("yaml"),
	ORTHOLOGY("json"),
	ALLELE_ASSOCIATION("json"),
	CONSTRUCT_ASSOCIATION("json"),
	VARIANT("json");
	
	public String fileExtension;
	
	private BackendBulkLoadType(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
