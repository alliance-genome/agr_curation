package org.alliancegenome.curation_api.enums;

public enum BackendBulkDataProvider {

	RGD("NCBITaxon:10116", "RGD:", 10116),
	MGI("NCBITaxon:10090", "MGI:", 10090),
	SGD("NCBITaxon:4932", "SGD:", 4932),
	OMIM("NCBITaxon:9606", "HGNC:", 9606),
	ZFIN("NCBITaxon:7955", "ZFIN:", 7955),
	FB("NCBITaxon:7227", "FB:", 7227),
	WB("NCBITaxon:6239", "WB:", 6239),
	XB("NCBITaxon:8355", "XB:", 8355);

	public String canonicalTaxonCurie;
	public String curiePrefix;
	public Integer idPart;
	
	private BackendBulkDataProvider(String canonicalTaxonCurie, String curiePrefix, Integer idPart) {
		this.canonicalTaxonCurie = canonicalTaxonCurie;
		this.curiePrefix = curiePrefix;
		this.idPart = idPart;
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
	
	public static String getCuriePrefixFromTaxonId(Integer id) {
		for (BackendBulkDataProvider provider : values()) {
			if (id.equals(provider.idPart)) {
				return provider.curiePrefix;
			}
		}
		return null;
	}
}