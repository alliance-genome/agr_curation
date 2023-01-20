package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.HashMap;
import java.util.Map;

public abstract class DiseaseAnnotationCurieManager {

	static Map<String, DiseaseAnnotationCurie> curieSpeciesNameMap = new HashMap<>();

	static {
		curieSpeciesNameMap.put("Danio rerio", new ZFINDiseaseAnnotationCurie());
		curieSpeciesNameMap.put("Homo sapiens", new RGDDiseaseAnnotationCurie());
		curieSpeciesNameMap.put("Rattus norvegicus", new RGDDiseaseAnnotationCurie());
		curieSpeciesNameMap.put("Mus musculus", new MGIDiseaseAnnotationCurie());
		curieSpeciesNameMap.put("Caenorhabditis elegans", new WormDiseaseAnnotationCurie());
		curieSpeciesNameMap.put("Drosophila melanogaster", new FlyDiseaseAnnotationCurie());
		curieSpeciesNameMap.put("Saccharomyces cerevisiae", new SGDDiseaseAnnotationCurie());
	}

	public static DiseaseAnnotationCurie getDiseaseAnnotationCurie(String speciesName) {
		DiseaseAnnotationCurie curie = curieSpeciesNameMap.get(speciesName);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + speciesName);
		return curie;
	}

	public static DiseaseAnnotationCurie getDiseaseAnnotationUniqueId(String speciesName) {
		DiseaseAnnotationCurie curie = curieSpeciesNameMap.get(speciesName);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + speciesName);
		return curie;
	}

}
