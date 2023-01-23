package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.enums.SupportedSpecies;

public abstract class DiseaseAnnotationCurieManager {

	public static DiseaseAnnotationCurie getDiseaseAnnotationCurie(String speciesName) {
		DiseaseAnnotationCurie curie = SupportedSpecies.getAnnotationCurie(speciesName);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + speciesName);
		return curie;
	}

	public static DiseaseAnnotationCurie getDiseaseAnnotationUniqueId(String speciesName) {
		DiseaseAnnotationCurie curie = SupportedSpecies.getAnnotationCurie(speciesName);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + speciesName);
		return curie;
	}

}
