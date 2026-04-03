package org.alliancegenome.curation_api.model.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorSerializer extends StdConverter<Set<OntologyTermClosure>, List<Pair<String, Set<String>>>> {

	@Override
	public List<Pair<String, Set<String>>> convert(Set<OntologyTermClosure> value) {
		List<Pair<String, Set<String>>> ancestorCuries = new ArrayList<>();
		if (value != null) {
			for (OntologyTermClosure closure : value) {
				if (closure.getClosureObject() != null && closure.getClosureObject().getCurie() != null) {
					ancestorCuries.add(Pair.of(closure.getClosureObject().getCurie(), closure.getClosureTypes()));
				}
			}
		}
		return ancestorCuries;
	}

}
