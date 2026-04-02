package org.alliancegenome.curation_api.model.serializers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorDeserializer extends StdConverter<HashMap<String, Set<String>>, Set<OntologyTermClosure>> {

	@Override
	public Set<OntologyTermClosure> convert(HashMap<String, Set<String>> curies) {
		Set<OntologyTermClosure> closures = new HashSet<>();
		if (curies != null) {
			for (Map.Entry<String, Set<String>> entry : curies.entrySet()) {
				OntologyTerm ancestorTerm = new OntologyTerm();
				ancestorTerm.setCurie(entry.getKey());
				OntologyTermClosure closure = new OntologyTermClosure();
				closure.setClosureObject(ancestorTerm);
				closure.setClosureTypes(entry.getValue());
				closures.add(closure);
			}
		}
		return closures;
	}

}
