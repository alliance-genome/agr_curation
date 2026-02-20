package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.GODocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.GODocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.GOSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;

import jakarta.inject.Inject;

public class GODocumentController implements GODocumentInterface {

	@Inject GoTermService goTermService;
	@Inject ResourceDescriptorPageService resourceDescriptorPageService;

	@Override
	public SearchResponse<GOSearchResultDocument> findSearchResult(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<GOTerm> resp = goTermService.findByParams(pagination, params);
		ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageService.getPageForResourceDescriptor("GO", "gene/interactions");

		ArrayList<GOSearchResultDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			GODocumentBuilder builder = new GODocumentBuilder();
			for (GOTerm goTerm : resp.getResults()) {
				GOSearchResultDocument doc = builder.buildSearchResultDocument(goTerm, resourceDescriptorPage);
				list.add(doc);
			}
		}

		SearchResponse<GOSearchResultDocument> ret = new SearchResponse<GOSearchResultDocument>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = goTermService.getAllGOSearchResultIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<GOSearchResultDocument> findByIds(List<Long> ids) {
		List<GOTerm> goTerms = goTermService.findByIds(ids);
		ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageService.getPageForResourceDescriptor("GO", "gene/interactions");

		ArrayList<GOSearchResultDocument> list = new ArrayList<>();
		if (goTerms != null) {
			GODocumentBuilder builder = new GODocumentBuilder();
			for (GOTerm goTerm : goTerms) {
				GOSearchResultDocument doc = builder.buildSearchResultDocument(goTerm, resourceDescriptorPage);
				list.add(doc);
			}
		}

		SearchResponse<GOSearchResultDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults((long) list.size());
		return ret;
	}
}
