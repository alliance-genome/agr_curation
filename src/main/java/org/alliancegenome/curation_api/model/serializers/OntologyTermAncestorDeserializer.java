package org.alliancegenome.curation_api.model.serializers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorDeserializer extends StdConverter<List<List<Object>>, Set<OntologyTermClosure>> {

	@Override
	@SuppressWarnings("unchecked")
	public Set<OntologyTermClosure> convert(List<List<Object>> pairs) {
		Set<OntologyTermClosure> closures = new HashSet<>();
		if (pairs != null) {
			for (List<Object> pair : pairs) {
				if (pair == null || pair.size() < 2) {
					continue;
				}
				String curie = (String) pair.get(0);
				Set<String> types = new HashSet<>((Collection<String>) pair.get(1));
				OntologyTerm ancestorTerm = new OntologyTerm();
				ancestorTerm.setCurie(curie);
				OntologyTermClosure closure = new OntologyTermClosure();
				closure.setClosureObject(ancestorTerm);
				closure.setClosureTypes(types);
				closures.add(closure);
			}
		}
		return closures;
	}

}
