package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.VariantDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.VariantDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.VariantSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VariantService;

import jakarta.inject.Inject;

public class VariantDocumentController implements VariantDocumentInterface {

	@Inject
	VariantService service;

	@Override
	public SearchResponse<VariantSummaryDocument> findDocuments(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

//		params.put("curatedVariantGenomicLocations.hgvs", "NC_007112.7:g.67388G>A");
		Pagination pagination = new Pagination(page, limit);
		SearchResponse<Variant> resp = service.findByParams(pagination, params);

		ArrayList<VariantSummaryDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			VariantDocumentBuilder builder = new VariantDocumentBuilder();
			for (Variant variant : resp.getResults()) {
				List<VariantSummaryDocument> docs = builder.buildVariantDocument(variant);
				list.addAll(docs);
			}
		}

		SearchResponse<VariantSummaryDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
}
