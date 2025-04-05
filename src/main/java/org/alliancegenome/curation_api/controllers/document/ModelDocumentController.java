package org.alliancegenome.curation_api.controllers.document;

import jakarta.inject.Inject;
import org.alliancegenome.curation_api.interfaces.document.ModelDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.ModelDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.ModelDocument;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelDocumentController implements ModelDocumentInterface {

	@Inject
	AffectedGenomicModelService service;

	@Override
	public SearchResponse<ModelDocument> findDocuments(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<AffectedGenomicModel> resp = service.findByParams(pagination, params);

		ArrayList<ModelDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			ModelDocumentBuilder builder = new ModelDocumentBuilder();
			for (AffectedGenomicModel gene : resp.getResults()) {
				ModelDocument doc = builder.buildModelDocument(gene);
				list.add(doc);
			}
		}

		SearchResponse<ModelDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
}
