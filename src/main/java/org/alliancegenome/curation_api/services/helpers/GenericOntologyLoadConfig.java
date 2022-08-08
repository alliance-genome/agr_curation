package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GenericOntologyLoadConfig {

	// These values should not be changed here
	// Only change them in the bulk controller
	// that is going to make use of the GenericOntologyLoader
	private ArrayList<String> altNameSpaces = new ArrayList<>();
	
	// must be set and will only load that Prefix of terms
	private String loadOnlyIRIPrefix = null;
	
	// do not load entities that have ChEBI equivalents
	private Boolean ignoreEntitiesWithChebiXref = false;

}
