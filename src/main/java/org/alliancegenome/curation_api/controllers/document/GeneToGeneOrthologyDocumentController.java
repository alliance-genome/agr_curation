package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.GeneToGeneOrthologyDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GeneToGeneOrthologyDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GeneToGeneOrthologyDocument;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;

import jakarta.inject.Inject;

public class GeneToGeneOrthologyDocumentController implements GeneToGeneOrthologyDocumentInterface {

	@Inject
	GeneToGeneOrthologyGeneratedService geneToGeneOrthologyGeneratedService;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = geneToGeneOrthologyGeneratedService.getAllOrthologyIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<GeneToGeneOrthologyDocument> findByIds(List<Long> ids) {
		List<GeneToGeneOrthologyGenerated> entities = geneToGeneOrthologyGeneratedService.findByIds(ids);

		ArrayList<GeneToGeneOrthologyDocument> list = new ArrayList<>();
		if (entities != null) {
			GeneToGeneOrthologyDocumentBuilder builder = new GeneToGeneOrthologyDocumentBuilder();
			for (GeneToGeneOrthologyGenerated entity : entities) {
				GeneToGeneOrthologyDocument doc = builder.buildSearchResultDocument(entity);
				list.add(doc);
			}
		}

		SearchResponse<GeneToGeneOrthologyDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults((long) list.size());
		return ret;
	}
}
