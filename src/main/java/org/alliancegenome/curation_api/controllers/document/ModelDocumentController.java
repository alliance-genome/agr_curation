package org.alliancegenome.curation_api.controllers.document;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.ModelDocumentInterface;
import org.alliancegenome.curation_api.model.document.es.ModelSearchResultDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

import jakarta.inject.Inject;

public class ModelDocumentController implements ModelDocumentInterface {

	@Inject
	AffectedGenomicModelService service;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = service.getAllIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<ModelSearchResultDocument> findSummaryByIds(List<Long> ids) {
		return service.findAgmsForSummaryByIds(ids);
	}
}