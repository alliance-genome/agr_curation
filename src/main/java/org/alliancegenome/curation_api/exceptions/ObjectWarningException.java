package org.alliancegenome.curation_api.exceptions;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;

import lombok.Getter;

@Getter
public class ObjectWarningException extends Exception {

	private final Long entityId;
	private final ObjectUpdateExceptionData data;
	private final List<String> skippedItems;
	private final String itemType;

	public ObjectWarningException(Long entityId, Object processedObject, String itemType, List<String> skippedItems) {
		super("Partial success: skipped " + skippedItems.size() + " invalid " + itemType);
		this.entityId = entityId;
		this.itemType = itemType;
		this.skippedItems = skippedItems;
		this.data = new ObjectUpdateExceptionData(
			processedObject,
			ValidationConstants.WARNING_MESSAGE + itemType + ": " + String.join(", ", skippedItems),
			null
		);
	}
}
