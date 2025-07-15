package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.interfaces.document.AlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.AlleleSummaryDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.inject.Inject;

public class AlleleDocumentController implements AlleleDocumentInterface {

	@Inject
	AlleleService alleleService;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	@Override
	public SearchResponse<AlleleSummaryDocument> findSummary(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<Allele> resp = alleleService.findByParams(pagination, params);

		List<String> sources = List.of("RGD", "MGI", "ZFIN", "FB", "WB", "SGD");
		ArrayList<AlleleSummaryDocument> list = new ArrayList<>();
		
		if (resp.getResults() != null) {
			AlleleSummaryDocumentBuilder builder = new AlleleSummaryDocumentBuilder();
			for (Allele allele : resp.getResults()) {
				AlleleSummaryDocument doc = builder.buildSummaryDocument(allele);
				
				// Add source reference links similar to disease controller
				doc.getAdditionalInformation().addAll(buildSourceReferenceLinkUrls(allele, sources));
				
				list.add(doc);
			}
		}

		SearchResponse<AlleleSummaryDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
	
	private List<Map<String, String>> buildSourceReferenceLinkUrls(Allele allele, List<String> sources) {
		List<Map<String, String>> sourceLinks = new ArrayList<>();
		
		for (String source : sources) {
			String pageName = "allele"; // Default page name for allele links
			
			// Check if this is the allele's data provider
			if (allele.getDataProvider() != null && 
				source.equals(allele.getDataProvider().getAbbreviation())) {
				
				ResourceDescriptorPage resourceDescriptorPage = resourceDescriptorPageService.getPageForResourceDescriptor(source, pageName);
				if (resourceDescriptorPage != null) {
					String url = resourceDescriptorPage.getUrlTemplate().replace("[%s]", allele.getPrimaryExternalId());
					Map<String, String> map = new HashMap<>();
					map.put("source", source);
					map.put("url", url);
					sourceLinks.add(map);
				}
			}
		}
		
		return sourceLinks;
	}
}