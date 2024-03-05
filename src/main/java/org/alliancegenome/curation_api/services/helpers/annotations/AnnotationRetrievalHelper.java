package org.alliancegenome.curation_api.services.helpers.annotations;

import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;

public abstract class AnnotationRetrievalHelper {

	public static <E extends Annotation> E getCurrentAnnotation(E annotation, SearchResponse<E> annotationList) {
		if (annotationList == null || CollectionUtils.isEmpty(annotationList.getResults()))
			return annotation;
		
		for (E annotationInList : annotationList.getResults()) {
			if (!annotationInList.getObsolete())
				return annotationInList;
		}
		
		return annotationList.getResults().get(0);
	}

}
