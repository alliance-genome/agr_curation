package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.document.HTPDatasetDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.HTPDatasetDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.HTPDatasetSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.HTPExpressionDatasetAnnotationService;

import jakarta.inject.Inject;

public class HTPDatasetDocumentController implements HTPDatasetDocumentInterface {

	@Inject HTPExpressionDatasetAnnotationService htpDatasetService;

	@Override
	public SearchResponse<HTPDatasetSearchResultDocument> findSearchResult(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<HTPExpressionDatasetAnnotation> resp = htpDatasetService.findByParams(pagination, params);

		ArrayList<HTPDatasetSearchResultDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			for (HTPExpressionDatasetAnnotation datasetAnnotation : resp.getResults()) {
				HTPDatasetSearchResultDocument doc = HTPDatasetDocumentBuilder.buildSearchResultDocument(datasetAnnotation);
				list.add(doc);
			}
		}

		SearchResponse<HTPDatasetSearchResultDocument> ret = new SearchResponse<HTPDatasetSearchResultDocument>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
}
