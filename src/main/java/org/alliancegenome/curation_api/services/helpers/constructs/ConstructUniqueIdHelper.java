package org.alliancegenome.curation_api.services.helpers.constructs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections.CollectionUtils;

public abstract class ConstructUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getConstructUniqueId(ConstructDTO constructDTO, List<String> refCuries) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(constructDTO.getName());
		uniqueId.add(constructDTO.getTaxonCurie());

		if (CollectionUtils.isNotEmpty(refCuries)) {
			Collections.sort(refCuries);
			uniqueId.addAll(refCuries);
		}
		
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
		uniqueId.add(construct.getName());
		if (construct.getTaxon() != null)
			uniqueId.add(construct.getTaxon().getCurie());
		
		if (CollectionUtils.isNotEmpty(construct.getReferences())) {
			List<String> refCuries = construct.getReferences().stream().map(Reference::getCurie)
		              .collect(Collectors.toList());
			Collections.sort(refCuries);
			uniqueId.addAll(refCuries);
		}
		
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