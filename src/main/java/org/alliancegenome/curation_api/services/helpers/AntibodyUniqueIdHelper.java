package org.alliancegenome.curation_api.services.helpers;

import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.ingest.dto.AntibodyDTO;

public abstract class AntibodyUniqueIdHelper {

	public static String getAntibodyUniqueId(AntibodyDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getName());
		uniqueId.add(dto.getClonalityName());
		uniqueId.addList(dto.getAntibodyTargetGeneIdentifiers());
		return uniqueId.getUniqueId();
	}

	public static String getAntibodyUniqueId(Antibody antibody) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(antibody.getName());
		if (antibody.getClonality() != null) {
			uniqueId.add(antibody.getClonality().getName());
		}
		uniqueId.addSubmittedObjectList(antibody.getAntibodyTargetGenes());
		return uniqueId.getUniqueId();
	}

}
