package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.document.GeneDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneService;

import jakarta.inject.Inject;

public class GeneDocumentController implements GeneDocumentInterface {

	@Inject GeneService geneService;

	@Override
	public SearchResponse<GeneSearchResultDocument> find(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<Gene> resp = geneService.findByParams(pagination, params);

		ArrayList<GeneSearchResultDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			GeneDocumentBuilder builder = new GeneDocumentBuilder();
			for (Gene gene : resp.getResults()) {
				GeneSearchResultDocument doc = builder.buildDocument(gene);
				list.add(doc);
			}
		}

		SearchResponse<GeneSearchResultDocument> ret = new SearchResponse<GeneSearchResultDocument>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
}
