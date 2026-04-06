package org.alliancegenome.curation_api.model.serializers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorDeserializer extends StdConverter<List<Map<String, List<String>>>, Set<OntologyTermClosure>> {

	@Override
	public Set<OntologyTermClosure> convert(List<Map<String, List<String>>> ancestors) {
		Set<OntologyTermClosure> closures = new HashSet<>();
		if (ancestors != null) {
			for (Map<String, List<String>> entry : ancestors) {
				if (entry == null) {
					continue;
				}
				for (Map.Entry<String, List<String>> pair : entry.entrySet()) {
					OntologyTerm ancestorTerm = new OntologyTerm();
					ancestorTerm.setCurie(pair.getKey());
					OntologyTermClosure closure = new OntologyTermClosure();
					closure.setClosureObject(ancestorTerm);
					closure.setClosureTypes(new HashSet<>(pair.getValue()));
					closures.add(closure);
				}
			}
		}
		return closures;
	}

}
