package org.alliancegenome.curation_api.services.helpers.constructs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections.CollectionUtils;

public abstract class ConstructUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getConstructUniqueId(ConstructDTO constructDTO) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (constructDTO.getConstructSymbolDto() != null)
			uniqueId.add(constructDTO.getConstructSymbolDto().getFormatText());
		if (constructDTO.getConstructFullNameDto() != null)
			uniqueId.add(constructDTO.getConstructFullNameDto().getFormatText());

		if (CollectionUtils.isNotEmpty(constructDTO.getConstructComponentDtos())) {
			List<String> componentIds = new ArrayList<>();
			for (ConstructComponentSlotAnnotationDTO componentDTO : constructDTO.getConstructComponentDtos()) {
				componentIds.add(getConstructComponentUniqueId(componentDTO));
			}
			Collections.sort(componentIds);
			uniqueId.addAll(componentIds);
		}
		
		return uniqueId.getUniqueId();
	}

	public static String getConstructUniqueId(Construct construct) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (construct.getConstructSymbol() != null)
			uniqueId.add(construct.getConstructSymbol().getFormatText());
		if (construct.getConstructFullName() != null)
			uniqueId.add(construct.getConstructFullName().getFormatText());
		
		if (CollectionUtils.isNotEmpty(construct.getConstructComponents())) {
			List<String> componentIds = new ArrayList<>();
			for (ConstructComponentSlotAnnotation component : construct.getConstructComponents()) {
				componentIds.add(getConstructComponentUniqueId(component));
			}
			Collections.sort(componentIds);
			uniqueId.addAll(componentIds);
		}
		
		return uniqueId.getUniqueId();
	}
	
	private static String getConstructComponentUniqueId(ConstructComponentSlotAnnotationDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getComponentSymbol());
		uniqueId.add(dto.getTaxonCurie());
		uniqueId.add(dto.getTaxonText());
		
		return uniqueId.getUniqueId();
	}
	
	private static String getConstructComponentUniqueId(ConstructComponentSlotAnnotation component) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(component.getComponentSymbol());
		if (component.getTaxon() != null)
			uniqueId.add(component.getTaxon().getCurie());
		uniqueId.add(component.getTaxonText());
		
		return uniqueId.getUniqueId();
	}

}
