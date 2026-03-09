package org.alliancegenome.curation_api.controllers.document;

import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.GeneMolecularInteractionDocumentInterface;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;

import jakarta.inject.Inject;

public class GeneMolecularInteractionDocumentController implements GeneMolecularInteractionDocumentInterface {

	@Inject
	GeneMolecularInteractionService geneMolecularInteractionService;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = geneMolecularInteractionService.getAllIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<GeneMolecularInteraction> findByIds(List<Long> ids) {
		List<GeneMolecularInteraction> entities = geneMolecularInteractionService.findByIds(ids);
		SearchResponse<GeneMolecularInteraction> response = new SearchResponse<>(entities);
		response.setTotalResults((long) entities.size());
		return response;
	}
}
