package org.alliancegenome.curation_api.model.serializers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorDeserializer extends StdConverter<List<Pair<String, Set<String>>>, Set<OntologyTermClosure>> {

	@Override
	public Set<OntologyTermClosure> convert(List<Pair<String, Set<String>>> curies) {
		Set<OntologyTermClosure> closures = new HashSet<>();
		if (curies != null) {
			for (Pair<String, Set<String>> pair : curies) {
				OntologyTerm ancestorTerm = new OntologyTerm();
				ancestorTerm.setCurie(pair.getLeft());
				OntologyTermClosure closure = new OntologyTermClosure();
				closure.setClosureObject(ancestorTerm);
				closure.setClosureTypes(pair.getRight());
				closures.add(closure);
			}
		}
		return closures;
	}

}
