package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.HashMap;
import java.util.Map;

public abstract class DiseaseAnnotationCurieManager {

	static Map<String, DiseaseAnnotationCurie> curieTaxonMap = new HashMap<>();

	static {
		curieTaxonMap.put("NCBITaxon:7955", new ZFINDiseaseAnnotationCurie());
		curieTaxonMap.put("NCBITaxon:9606", new RGDDiseaseAnnotationCurie());
		curieTaxonMap.put("NCBITaxon:10116", new RGDDiseaseAnnotationCurie());
		curieTaxonMap.put("NCBITaxon:10090", new MGIDiseaseAnnotationCurie());
		curieTaxonMap.put("NCBITaxon:6239", new WormDiseaseAnnotationCurie());
		curieTaxonMap.put("NCBITaxon:7227", new FlyDiseaseAnnotationCurie());
		curieTaxonMap.put("NCBITaxon:559292", new SGDDiseaseAnnotationCurie());
	}

	public static DiseaseAnnotationCurie getDiseaseAnnotationCurie(String taxonID) {
		DiseaseAnnotationCurie curie = curieTaxonMap.get(taxonID);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + taxonID);
		return curie;
	}

	public static DiseaseAnnotationCurie getDiseaseAnnotationUniqueId(String taxonID) {
		DiseaseAnnotationCurie curie = curieTaxonMap.get(taxonID);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + taxonID);
		return curie;
	}

}
