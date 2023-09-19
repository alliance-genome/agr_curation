package org.alliancegenome.curation_api.services.helpers.constructs;

import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

public abstract class ConstructUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getConstructUniqueId(ConstructDTO constructDTO) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(constructDTO.getName());

		//uniqueId.addList(annotationDTO.getDiseaseGeneticModifierCuries());
		return uniqueId.getUniqueId();
	}

	public static String getConstructUniqueId(Construct construct) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(construct.getName());
		
		return uniqueId.getUniqueId();
	}

}
