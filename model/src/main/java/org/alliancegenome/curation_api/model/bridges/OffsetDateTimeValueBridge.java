package org.alliancegenome.curation_api.model.bridges;

import java.time.OffsetDateTime;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;


public class OffsetDateTimeValueBridge implements ValueBridge<OffsetDateTime, String> {
	@Override
	public String toIndexedValue(OffsetDateTime value, ValueBridgeToIndexedValueContext context) {
		if(value == null)
			return null;
		return value.toString();
	}
}
