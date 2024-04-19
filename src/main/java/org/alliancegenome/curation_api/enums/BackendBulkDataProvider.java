package org.alliancegenome.curation_api.enums;

public enum BackendBulkDataProvider {

	RGD("NCBITaxon:10116", "RGD:", 10116, "RGD", "RGD", false, false, false, false),
	MGI("NCBITaxon:10090", "MGI:", 10090, "MGI", "MGI", true, false, true, false),
	SGD("NCBITaxon:4932", "SGD:", 4932, "SGD", "SGD", true, false, false, false ), //TODO: needs checking
	HUMAN("NCBITaxon:9606", "HGNC:", 9606, "RGD", "RGD", false, false, false, false),
	ZFIN("NCBITaxon:7955", "ZFIN:", 7955, "ZFIN", "ZFIN", true, false, true, false),
	FB("NCBITaxon:7227", "FB:", 7227, "FB", "FB", true, false, false, false),
	WB("NCBITaxon:6239", "WB:", 6239, "WB", "WB", true, false, false, true),
	XB("NCBITaxon:8355", "XB:", 8355, "XB", "Xenbase", true, false, false, false), //TODO: needs checking
	XBXL("NCBITaxon:8355", "XB:", 8355, "XB", "Xenbase", true, false, false, false), //TODO: needs checking
	XBXT("NCBITaxon:8364", "XB:", 8364, "XB", "Xenbase", true, false, false, false); //TODO: needs checking

	public String canonicalTaxonCurie;
	public String curiePrefix;
	public Integer idPart;
	public String sourceOrganization;	//The Source Organisation Abbreviation
	public String resourceDescriptor;	//The organisation's name in the ResourceDescriptor file
	public Boolean hasInferredGenePhenotypeAnnotations;
	public Boolean hasAssertedGenePhenotypeAnnotations;
	public Boolean hasInferredAllelePhenotypeAnnotations;
	public Boolean hasAssertedAllelePhenotypeAnnotations;

	private BackendBulkDataProvider(String canonicalTaxonCurie, String curiePrefix, Integer idPart, String sourceOrganization, String resourceDescriptor,
			Boolean hasInferredGenePhenotypeAnnotations, Boolean hasAssertedGenePhenotypeAnnotations, Boolean hasInferredAllelePhenotypeAnnotations,
			Boolean hasAssertedAllelePhenotypeAnnotations) {
		this.canonicalTaxonCurie = canonicalTaxonCurie;
		this.curiePrefix = curiePrefix;
		this.idPart = idPart;
		this.sourceOrganization = sourceOrganization;
		this.resourceDescriptor = resourceDescriptor;
		this.hasInferredGenePhenotypeAnnotations = hasInferredGenePhenotypeAnnotations;
		this.hasAssertedGenePhenotypeAnnotations = hasAssertedGenePhenotypeAnnotations;
		this.hasInferredAllelePhenotypeAnnotations = hasInferredAllelePhenotypeAnnotations;
		this.hasAssertedAllelePhenotypeAnnotations = hasAssertedAllelePhenotypeAnnotations;
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