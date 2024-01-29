package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;

public abstract class DiseaseAnnotationRetrievalHelper {

	public static <E extends DiseaseAnnotation> E getCurrentDiseaseAnnotation(E annotation, SearchResponse<E> annotationList) {
		if (annotationList == null || CollectionUtils.isEmpty(annotationList.getResults()))
			return annotation;
		
		for (E annotationInList : annotationList.getResults()) {
			if (!annotationInList.getObsolete())
				return annotationInList;
		}
		
		return annotationList.getResults().get(0);
	}

}
