package org.alliancegenome.curation_api.model.serializers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class OntologyTermAncestorDeserializer extends JsonDeserializer<Set<OntologyTermClosure>> {

	@Override
	public Set<OntologyTermClosure> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		Set<OntologyTermClosure> closures = new HashSet<>();
		if (p.currentToken() == JsonToken.START_ARRAY) {
			while (p.nextToken() != JsonToken.END_ARRAY) {
				String curie = p.getText();
				OntologyTerm term = new OntologyTerm();
				term.setCurie(curie);
				OntologyTermClosure closure = new OntologyTermClosure();
				closure.setClosureObject(term);
				closures.add(closure);
			}
		}
		return closures;
	}

}
