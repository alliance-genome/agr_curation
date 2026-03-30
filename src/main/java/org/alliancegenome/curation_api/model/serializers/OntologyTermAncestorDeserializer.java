package org.alliancegenome.curation_api.model.serializers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
				// Skip the curie strings - they are only needed for ES indexing
			}
		}
		return closures;
	}

}
