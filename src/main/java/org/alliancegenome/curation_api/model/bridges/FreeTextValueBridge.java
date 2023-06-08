package org.alliancegenome.curation_api.model.bridges;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class FreeTextValueBridge implements ValueBridge<String, String> {
	@Override
	public String toIndexedValue(String value, ValueBridgeToIndexedValueContext context) {
		if (value == null)
			return null;
		return value.length() > 1000 ? value.substring(0, 1000) : value;
	}
}
