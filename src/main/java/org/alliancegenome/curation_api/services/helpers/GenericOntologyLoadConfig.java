package org.alliancegenome.curation_api.services.helpers;

import java.util.HashSet;

import lombok.Data;

@Data
public class GenericOntologyLoadConfig {

	// These values should not be changed here
	// Only change them in the bulk controller
	// that is going to make use of the GenericOntologyLoader
	private HashSet<String> altNameSpaces = new HashSet<>();
	
	// must be set and will only load that Prefix of terms
	private String loadOnlyIRIPrefix = null;
	
	// do not load entities that have ChEBI equivalents
	private Boolean ignoreEntitiesWithChebiXref = false;
	
	// Can be used to turn of loading of ancestors on large ontologies
	private Boolean loadAncestors = true;

}
