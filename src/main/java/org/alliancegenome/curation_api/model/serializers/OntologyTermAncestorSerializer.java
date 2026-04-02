package org.alliancegenome.curation_api.model.serializers;

import java.util.HashMap;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorSerializer extends StdConverter<Set<OntologyTermClosure>, HashMap<String, Set<String>>> {

	@Override
	public HashMap<String, Set<String>> convert(Set<OntologyTermClosure> value) {
		HashMap<String, Set<String>> ancestorCuries = new HashMap<>();
		if (value != null) {
			for (OntologyTermClosure closure : value) {
				if (closure.getClosureObject() != null && closure.getClosureObject().getCurie() != null) {
					ancestorCuries.put(closure.getClosureObject().getCurie(), closure.getClosureTypes());
				}
			}
		}
		return ancestorCuries;
	}

}
