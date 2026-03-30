package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.AGMAnnotationDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.AGMAnnotationDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AGMAnnotationDocument;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

import jakarta.inject.Inject;

public class AGMAnnotationDocumentController implements AGMAnnotationDocumentInterface {

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
	public SearchResponse<AGMAnnotationDocument> findByIds(List<Long> ids) {
		List<AffectedGenomicModel> entities = affectedGenomicModelService.findByIds(ids);

		ArrayList<AGMAnnotationDocument> list = new ArrayList<>();
		if (entities != null) {
			AGMAnnotationDocumentBuilder builder = new AGMAnnotationDocumentBuilder();
			for (AffectedGenomicModel model : entities) {
				List<AGMAnnotationDocument> docs = builder.buildAGMAnntationDocument(model);
				list.addAll(docs);
			}
		}

		SearchResponse<AGMAnnotationDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults((long) list.size());
		return ret;
	}
}
