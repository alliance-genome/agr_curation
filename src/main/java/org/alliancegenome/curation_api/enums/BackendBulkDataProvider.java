package org.alliancegenome.curation_api.enums;

import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.FlyDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.MGIDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.RGDDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.SGDDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.WormDiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ZFINDiseaseAnnotationCurie;

public enum BackendBulkDataProvider {
	RGD(new RGDDiseaseAnnotationCurie()),
	MGI(new MGIDiseaseAnnotationCurie()),
	SGD(new SGDDiseaseAnnotationCurie()),
	OMIM(new RGDDiseaseAnnotationCurie()),
	ZFIN(new ZFINDiseaseAnnotationCurie()),
	FB(new FlyDiseaseAnnotationCurie()),
	WB(new WormDiseaseAnnotationCurie());

	public DiseaseAnnotationCurie annotationCurie;
	
	private BackendBulkDataProvider(DiseaseAnnotationCurie annotationCurie) {
		this.annotationCurie = annotationCurie;
	}
	
	public static DiseaseAnnotationCurie getAnnotationCurie(String dataProvider) {
		BackendBulkDataProvider result = null;
		for (BackendBulkDataProvider provider : values()) {
			if (provider.name().equals(dataProvider)) {
				result = provider;
				break;
			}
		}
		if (result == null)
			return null;
		return result.annotationCurie;
	}
}