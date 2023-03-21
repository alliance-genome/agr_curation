package org.alliancegenome.curation_api.enums;

public enum BackendBulkDataType {
	RGD("NCBITaxon:10116", "Rattus norvegicus", "RGD"), MGI("NCBITaxon:10090", "Mus musculus", "MGI"), SGD("NCBITaxon:559292", "Saccharomyces cerevisiae", "SGD"), HUMAN("NCBITaxon:9606", "Homo sapiens", "OMIM"), ZFIN("NCBITaxon:7955", "Danio rerio", "ZFIN"), FB("NCBITaxon:7227", "Drosophila melanogaster", "FB"), WB("NCBITaxon:6239", "Caenorhabditis elegans", "WB");

	public final String taxonId;
	public final String speciesName;
	public final String dataProviderAbbreviation;

	private BackendBulkDataType(String taxonId, String speciesName, String dataProviderAbbreviation) {
		this.taxonId = taxonId;
		this.speciesName = speciesName;
		this.dataProviderAbbreviation = dataProviderAbbreviation;
	}

	public String getTaxonId() {
		return this.taxonId;
	}
	
	public String getSpeciesName() {
		return this.speciesName;
	}
	
	public String getDataProviderAbbreviation() {
		return this.dataProviderAbbreviation;
	}
	
	public static String getDataProviderAbbreviationFromDataType(String dataTypeName) {
		BackendBulkDataType dataType = getDataType(dataTypeName);
		if (dataType == null)
			return null;
		return dataType.dataProviderAbbreviation;
	}
	
	public static String getSpeciesNameFromDataType(String dataTypeName) {
		BackendBulkDataType dataType = getDataType(dataTypeName);
		if (dataType == null)
			return null;
		return dataType.speciesName;
	}
	
	private static BackendBulkDataType getDataType(String dataTypeName) {
		BackendBulkDataType result = null;
		for (BackendBulkDataType dataType : values()) {
			if (dataType.name().equalsIgnoreCase(dataTypeName)) {
				result = dataType;
				break;
			}
		}
		return result;
	}
}