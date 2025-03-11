package org.alliancegenome.curation_api.model.bridges;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class IntegerValueBridge implements ValueBridge<Integer, String> {
	@Override
	public String toIndexedValue(Integer value, ValueBridgeToIndexedValueContext context) {
		return String.format("%1$13s", value.toString()).replace(' ', '0');
	}
}