package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.alliancegenome.curation_api.interfaces.document.GeneToGeneOrthologyDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneToGeneOrthologyDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneToGeneOrthologyDocument;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneExpressionAnnotationService;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;

import jakarta.inject.Inject;

public class GeneToGeneOrthologyDocumentController implements GeneToGeneOrthologyDocumentInterface {

	@Inject
	GeneToGeneOrthologyGeneratedService geneToGeneOrthologyGeneratedService;

	@Override
	public SearchResponse<GeneToGeneOrthologyDocument> findDocument(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}
		Pagination pagination = new Pagination(page, limit);
		SearchResponse<GeneToGeneOrthologyGenerated> resp = geneToGeneOrthologyGeneratedService.findByParams(pagination, params);

		ArrayList<GeneToGeneOrthologyDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			GeneToGeneOrthologyDocumentBuilder builder = new GeneToGeneOrthologyDocumentBuilder();
			for (GeneToGeneOrthologyGenerated geneToGeneOrthology : resp.getResults()) {
				GeneToGeneOrthologyDocument doc = builder.buildSearchResultDocument(geneToGeneOrthology);
				list.add(doc);
			}
		}

		SearchResponse<GeneToGeneOrthologyDocument> ret = new SearchResponse<GeneToGeneOrthologyDocument>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
}
