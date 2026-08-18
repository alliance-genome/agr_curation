package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.CassetteComponentSlotAnnotationDTO;
import org.apache.commons.collections.CollectionUtils;

public abstract class CassetteUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getCassetteUniqueId(CassetteDTO cassetteDTO) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (cassetteDTO.getCassetteSymbolDto() != null) {
			uniqueId.add(cassetteDTO.getCassetteSymbolDto().getFormatText());
		}
		if (cassetteDTO.getCassetteFullNameDto() != null) {
			uniqueId.add(cassetteDTO.getCassetteFullNameDto().getFormatText());
		}

		if (CollectionUtils.isNotEmpty(cassetteDTO.getCassetteComponentDtos())) {
			List<String> componentIds = new ArrayList<>();
			for (CassetteComponentSlotAnnotationDTO componentDTO : cassetteDTO.getCassetteComponentDtos()) {
				componentIds.add(getCassetteComponentUniqueId(componentDTO));
			}
			Collections.sort(componentIds);
			uniqueId.addAll(componentIds);
		}

		return uniqueId.getUniqueId();
	}

	public static String getCassetteUniqueId(Cassette cassette) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (cassette.getCassetteSymbol() != null) {
			uniqueId.add(cassette.getCassetteSymbol().getFormatText());
		}
		if (cassette.getCassetteFullName() != null) {
			uniqueId.add(cassette.getCassetteFullName().getFormatText());
		}

		if (CollectionUtils.isNotEmpty(cassette.getCassetteComponents())) {
			List<String> componentIds = new ArrayList<>();
			for (CassetteComponentSlotAnnotation component : cassette.getCassetteComponents()) {
				componentIds.add(getCassetteComponentUniqueId(component));
			}
			Collections.sort(componentIds);
			uniqueId.addAll(componentIds);
		}

		return uniqueId.getUniqueId();
	}

	private static String getCassetteComponentUniqueId(CassetteComponentSlotAnnotationDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getComponentSymbol());
		uniqueId.add(dto.getTaxonCurie());
		uniqueId.add(dto.getTaxonText());

		return uniqueId.getUniqueId();
	}

	private static String getCassetteComponentUniqueId(CassetteComponentSlotAnnotation component) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(component.getComponentSymbol());
		if (component.getTaxon() != null) {
			uniqueId.add(component.getTaxon().getCurie());
		}
		uniqueId.add(component.getTaxonText());

		return uniqueId.getUniqueId();
	}

}
