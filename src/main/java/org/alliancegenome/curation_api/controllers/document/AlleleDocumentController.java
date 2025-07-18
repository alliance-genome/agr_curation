package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.document.AlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.AlleleSummaryDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;

import jakarta.inject.Inject;

public class AlleleDocumentController implements AlleleDocumentInterface {

	@Inject
	AlleleService alleleService;
	@Inject
	AlleleSummaryDocumentBuilder alleleSummaryDocumentBuilder;

	@Override
	public SearchResponse<AlleleSummaryDocument> findSummary(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<Allele> resp = alleleService.findByParams(pagination, params);

		ArrayList<AlleleSummaryDocument> list = new ArrayList<>();

		if (resp.getResults() != null) {
			for (Allele allele : resp.getResults()) {
				AlleleSummaryDocument doc = alleleSummaryDocumentBuilder.buildSummaryDocument(allele);
				list.add(doc);
			}
		}

		SearchResponse<AlleleSummaryDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}

}
