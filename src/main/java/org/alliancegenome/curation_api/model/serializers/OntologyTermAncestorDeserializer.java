package org.alliancegenome.curation_api.model.serializers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.databind.util.StdConverter;

public class OntologyTermAncestorDeserializer extends StdConverter<List<String>, Set<OntologyTermClosure>> {

	@Override
	public Set<OntologyTermClosure> convert(List<String> curies) {
		Set<OntologyTermClosure> closures = new HashSet<>();
		if (curies != null) {
			for (String curie : curies) {
				OntologyTerm ancestorTerm = new OntologyTerm();
				ancestorTerm.setCurie(curie);
				OntologyTermClosure closure = new OntologyTermClosure();
				closure.setClosureObject(ancestorTerm);
				closures.add(closure);
			}
		}
		return closures;
	}

}
