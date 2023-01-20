package org.alliancegenome.curation_api.enums;

public enum BackendBulkDataType {
	RGD("NCBITaxon:10116", "Rattus norvegicus"), MGI("NCBITaxon:10090", "Mus musculus"), SGD("NCBITaxon:559292", "Saccharomyces cerevisiae"), HUMAN("NCBITaxon:9606", "Homo sapiens"), ZFIN("NCBITaxon:7955", "Danio rerio"), FB("NCBITaxon:7227", "Drosophila melanogaster"), WB("NCBITaxon:6239", "Caenorhabditis elegans");

	public final String taxonId;
	public final String speciesName;

	private BackendBulkDataType(String taxonId, String speciesName) {
		this.taxonId = taxonId;
		this.speciesName = speciesName;
	}

	public String getTaxonId() {
		return this.taxonId;
	}
	
	public String getSpeciesName() {
		return this.speciesName;
	}
}