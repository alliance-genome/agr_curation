package org.alliancegenome.curation_api.model.bridges;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class IntegerValueBridge implements ValueBridge<Integer, String> {
	@Override
	public String toIndexedValue(Integer value, ValueBridgeToIndexedValueContext context) {
		String padding = "0000000000000";
		if (value == null) {
			return padding;
		}
		String valueString = value.toString();
		
		return padding.substring(valueString.length()) + valueString;
	}
}