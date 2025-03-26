package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import org.alliancegenome.curation_api.interfaces.document.DiseaseDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.DiseaseSummaryDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.DiseaseSearchResultDocument;
import org.alliancegenome.curation_api.model.document.es.DiseaseSummaryDocument;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.ontology.DoTermService;

public class DiseaseDocumentController implements DiseaseDocumentInterface {

	@Inject
	DoTermService doTermService;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject
	DiseaseSummaryDocumentBuilder builder;

	@Override
	public SearchResponse<DiseaseSummaryDocument> findSummary(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<DOTerm> resp = doTermService.findByParams(pagination, params);

		List<String> sources = List.of("RGD", "MGI", "ZFIN", "FB", "WB", "SGD", "Xenbase");
		ArrayList<DiseaseSummaryDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			DiseaseSummaryDocumentBuilder builder = new DiseaseSummaryDocumentBuilder();
			for (DOTerm doTerm : resp.getResults()) {
				DiseaseSummaryDocument doc = builder.buildSummaryDocument(doTerm);
				doc.setSourceReferenceLinkUrls(new ArrayList<Map<String, String>>());
				String pageName;
				for (String source : sources) {
					if (source.equals("RGD")) {
						pageName = "disease/all";
					} else {
						pageName = "disease";
					}
					ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageService.getPageForResourceDescriptor(source, pageName);
					if (resourceDescriptorPage != null) {
						String url = resourceDescriptorPage.getUrlTemplate().replace("[%s]", doTerm.getCurie());
						Map<String, String> map = new HashMap<>();
						map.put("source", source);
						map.put("url", url);
						doc.getSourceReferenceLinkUrls().add(map);
					}
				}

				list.add(doc);
			}
		}

		SearchResponse<DiseaseSummaryDocument> ret = new SearchResponse<DiseaseSummaryDocument>(list);

		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}

	@Override
	public SearchResponse<DiseaseSearchResultDocument> findSearchResult(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<DOTerm> resp = doTermService.findByParams(pagination, params);

		ArrayList<DiseaseSearchResultDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			for (DOTerm doTerm : resp.getResults()) {
				DiseaseSearchResultDocument doc = builder.buildSearchResultDocument(doTerm);
				list.add(doc);
			}
		}

		SearchResponse<DiseaseSearchResultDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}


}
