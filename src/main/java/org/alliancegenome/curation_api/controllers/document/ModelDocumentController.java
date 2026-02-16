package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.ModelDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.ModelDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AffectedGenomicModelDocument;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

import jakarta.inject.Inject;

public class ModelDocumentController implements ModelDocumentInterface {

	@Inject
	AffectedGenomicModelService affectedGenomicModelService;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = affectedGenomicModelService.getAllIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<AffectedGenomicModelDocument> findByIds(List<Long> ids) {
		List<AffectedGenomicModel> entities = affectedGenomicModelService.findByIds(ids);

		ArrayList<AffectedGenomicModelDocument> list = new ArrayList<>();
		if (entities != null) {
			ModelDocumentBuilder builder = new ModelDocumentBuilder();
			for (AffectedGenomicModel model : entities) {
				List<AffectedGenomicModelDocument> docs = builder.buildModelDocument(model);
				list.addAll(docs);
			}
		}

		SearchResponse<AffectedGenomicModelDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults((long) list.size());
		return ret;
	}
}
