package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;

public abstract class DiseaseAnnotationCurieManager {

	public static DiseaseAnnotationCurie getDiseaseAnnotationUniqueId(String dataProvider) {
		DiseaseAnnotationCurie curie = BackendBulkDataProvider.getAnnotationCurie(dataProvider);
		if (curie == null)
			throw new RuntimeException("No Disease Annotation Curie definition found for " + dataProvider);
		return curie;
	}

}
