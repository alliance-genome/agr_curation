package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.TransgenicToolUseSlotAnnotationDTO;
import org.apache.commons.collections.CollectionUtils;

public abstract class TransgenicToolUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getTransgenicToolUniqueId(TransgenicToolDTO transgenicToolDTO) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (transgenicToolDTO.getTransgenicToolSymbolDto() != null) {
			uniqueId.add(transgenicToolDTO.getTransgenicToolSymbolDto().getFormatText());
		}
		if (transgenicToolDTO.getTransgenicToolFullNameDto() != null) {
			uniqueId.add(transgenicToolDTO.getTransgenicToolFullNameDto().getFormatText());
		}

		if (CollectionUtils.isNotEmpty(transgenicToolDTO.getTransgenicToolUseDtos())) {
			List<String> useIds = new ArrayList<>();
			for (TransgenicToolUseSlotAnnotationDTO useDTO : transgenicToolDTO.getTransgenicToolUseDtos()) {
				useIds.add(getTransgenicToolUseUniqueId(useDTO));
			}
			Collections.sort(useIds);
			uniqueId.addAll(useIds);
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

		if (CollectionUtils.isNotEmpty(transgenicTool.getTransgenicToolUses())) {
			List<String> useIds = new ArrayList<>();
			for (TransgenicToolUseSlotAnnotation use : transgenicTool.getTransgenicToolUses()) {
				useIds.add(getTransgenicToolUseUniqueId(use));
			}
			Collections.sort(useIds);
			uniqueId.addAll(useIds);
		}

		return uniqueId.getUniqueId();
	}

	private static String getTransgenicToolUseUniqueId(TransgenicToolUseSlotAnnotationDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getComponentSymbol());
		uniqueId.add(dto.getTaxonCurie());
		uniqueId.add(dto.getTaxonText());

		return uniqueId.getUniqueId();
	}

	private static String getTransgenicToolUseUniqueId(TransgenicToolUseSlotAnnotation use) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(use.getComponentSymbol());
		if (use.getTaxon() != null) {
			uniqueId.add(use.getTaxon().getCurie());
		}
		uniqueId.add(use.getTaxonText());

		return uniqueId.getUniqueId();
	}

}
