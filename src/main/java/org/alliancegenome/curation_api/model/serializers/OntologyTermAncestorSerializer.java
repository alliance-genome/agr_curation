package org.alliancegenome.curation_api.model.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorSerializer extends StdConverter<Set<OntologyTermClosure>, List<String>> {

	@Override
	public List<String> convert(Set<OntologyTermClosure> value) {
		List<String> ancestorCuries = new ArrayList<>();
		if (value != null) {
			for (OntologyTermClosure closure : value) {
				if (closure.getClosureObject() != null && closure.getClosureObject().getCurie() != null) {
					ancestorCuries.add(closure.getClosureObject().getCurie());
				}
			}
		}
		return ancestorCuries;
	}

}
