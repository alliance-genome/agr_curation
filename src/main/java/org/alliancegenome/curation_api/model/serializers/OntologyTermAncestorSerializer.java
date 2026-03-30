package org.alliancegenome.curation_api.model.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class OntologyTermAncestorSerializer extends JsonSerializer<Set<OntologyTermClosure>> {

	@Override
	public void serialize(Set<OntologyTermClosure> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		List<String> ancestorCuries = new ArrayList<>();
		if (value != null) {
			for (OntologyTermClosure closure : value) {
				if (closure.getClosureObject() != null && closure.getClosureObject().getCurie() != null) {
					ancestorCuries.add(closure.getClosureObject().getCurie());
				}
			}
		}
		gen.writeObject(ancestorCuries);
	}

}
