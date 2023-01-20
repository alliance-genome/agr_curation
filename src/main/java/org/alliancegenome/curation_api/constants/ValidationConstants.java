package org.alliancegenome.curation_api.constants;

import java.util.List;

public final class ValidationConstants {

	public static final String INVALID_MESSAGE = "Not a valid entry";
	public static final String OBSOLETE_MESSAGE = "Obsolete term specified";
	public static final String REQUIRED_MESSAGE = "Required field is empty";
	public static final String DEPENDENCY_MESSAGE_PREFIX = "Invalid without value for ";
	public static final String NON_UNIQUE_MESSAGE = "Field value is not unique";
	public static final String UNSUPPORTED_MESSAGE = "Unsupported value specified";

	public static final List<String> SUPPORTED_SPECIES = List.of(
			"Danio rerio",
			"Homo sapiens",
			"Saccharomyces cerevisiae",
			"Drosophila melanogaster",
			"Caenorhabditis elegans",
			"Mus musculus",
			"Rattus norvegicus"
			);
	
	public static final List<String> CANONICAL_TAXONS = List.of(
			"NCBITaxon:7955",
			"NCBITaxon:9606",
			"NCBITaxon:10116",
			"NCBITaxon:10090",
			"NCBITaxon:6239",
			"NCBITaxon:7227",
			"NCBITaxon:559292"
			);
}