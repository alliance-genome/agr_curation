package org.alliancegenome.curation_api.model.bridges;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class BooleanAndNullValueBridge implements ValueBridge<Boolean, String> {
	@Override
	public String toIndexedValue(Boolean value, ValueBridgeToIndexedValueContext context) {
		if(value == null)
			return null;
		return value ? "true" : "false";
	}
}
