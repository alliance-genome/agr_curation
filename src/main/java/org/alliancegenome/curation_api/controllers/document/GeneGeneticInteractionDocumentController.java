package org.alliancegenome.curation_api.controllers.document;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.GeneGeneticInteractionDocumentInterface;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;

import jakarta.inject.Inject;

public class GeneGeneticInteractionDocumentController implements GeneGeneticInteractionDocumentInterface {

	@Inject
	GeneGeneticInteractionService geneGeneticInteractionService;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = geneGeneticInteractionService.getAllIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<GeneGeneticInteraction> findByIds(List<Long> ids) {
		List<GeneGeneticInteraction> entities = geneGeneticInteractionService.findByIds(ids);
		SearchResponse<GeneGeneticInteraction> response = new SearchResponse<>(entities);
		response.setTotalResults((long) entities.size());
		return response;
	}
}
