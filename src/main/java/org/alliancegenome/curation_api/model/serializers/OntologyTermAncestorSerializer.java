package org.alliancegenome.curation_api.model.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorSerializer extends StdConverter<Set<OntologyTermClosure>, List<Map<String, Set<String>>>> {

	@Override
	public List<Map<String, Set<String>>> convert(Set<OntologyTermClosure> value) {
		List<Map<String, Set<String>>> ancestors = new ArrayList<>();
		if (value != null) {
			for (OntologyTermClosure closure : value) {
				if (closure.getClosureObject() != null && closure.getClosureObject().getCurie() != null) {
					ancestors.add(Map.of(closure.getClosureObject().getCurie(), closure.getClosureTypes()));
				}
			}
		}
		return ancestors;
	}

}
