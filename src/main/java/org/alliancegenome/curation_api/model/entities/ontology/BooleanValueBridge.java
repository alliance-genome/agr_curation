package org.alliancegenome.curation_api.model.entities.ontology;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class BooleanValueBridge implements ValueBridge<Boolean, String> {
    @Override
    public String toIndexedValue(Boolean value, ValueBridgeToIndexedValueContext context) {
        if(value == null)
            return "false";
        return value ? "true" : "false";
    }
}
