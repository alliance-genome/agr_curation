package org.alliancegenome.curation_api.services.helpers;

import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;

/**
 * SCRUM-6535.
 *
 * A transgenic tool has no components, so unlike a cassette or a construct its identity rests on
 * symbol and full name alone.
 */
public abstract class TransgenicToolUniqueIdHelper {

	public static String getTransgenicToolUniqueId(TransgenicToolDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (dto.getTransgenicToolSymbolDto() != null) {
			uniqueId.add(dto.getTransgenicToolSymbolDto().getFormatText());
		}
		if (dto.getTransgenicToolFullNameDto() != null) {
			uniqueId.add(dto.getTransgenicToolFullNameDto().getFormatText());
		}
		return uniqueId.getUniqueId();
	}

	public static String getTransgenicToolUniqueId(TransgenicTool transgenicTool) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (transgenicTool.getTransgenicToolSymbol() != null) {
			uniqueId.add(transgenicTool.getTransgenicToolSymbol().getFormatText());
		}
		if (transgenicTool.getTransgenicToolFullName() != null) {
			uniqueId.add(transgenicTool.getTransgenicToolFullName().getFormatText());
		}
		return uniqueId.getUniqueId();
	}

}
