package org.alliancegenome.curation_api.enums;

public enum BackendBulkDataProvider {
	RGD("NCBITaxon:10116", "RGD:"),
	MGI("NCBITaxon:10090", "MGI:"),
	SGD("NCBITaxon:4932", "SGD:"),
	OMIM("NCBITaxon:9606", "HGNC:"),
	ZFIN("NCBITaxon:7955", "ZFIN:"),
	FB("NCBITaxon:7227", "FB:"),
	WB("NCBITaxon:6239", "WB:");
	
	public String canonicalTaxonCurie;
	public String curiePrefix;
	
	private BackendBulkDataProvider(String canonicalTaxonCurie, String curiePrefix) {
		this.canonicalTaxonCurie = canonicalTaxonCurie;
		this.curiePrefix = curiePrefix;
	}
	
	
	public static String getCanonicalTaxonCurie(String dataProvider) {
		BackendBulkDataProvider result = null;
		for (BackendBulkDataProvider provider : values()) {
			if (provider.name().equals(dataProvider)) {
				result = provider;
				break;
			}
		}
		if (result == null)
			return null;
		return result.canonicalTaxonCurie;
	}
	
	public static String getCuriePrefixFromTaxonCurie(String taxonCurie) {
		BackendBulkDataProvider result = null;
		for (BackendBulkDataProvider provider : values()) {
			if (provider.canonicalTaxonCurie.equals(taxonCurie)) {
				result = provider;
				break;
			}
		}
		if (result == null)
			return null;
		return result.curiePrefix;
	}
}