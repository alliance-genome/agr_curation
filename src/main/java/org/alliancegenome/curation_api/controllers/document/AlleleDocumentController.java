package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.AlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.AlleleSummaryDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDTO;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class AlleleDocumentController implements AlleleDocumentInterface {

	@Inject
	AlleleService alleleService;

	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = alleleService.getAllAlleleSummaryIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<AlleleSummaryDocument> findSummaryByIds(List<Long> ids) {
		SearchResponse<AlleleSummaryDTO> resp = alleleService.findAllelesForSummaryByIds(ids);
		ArrayList<AlleleSummaryDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			AlleleSummaryDocumentBuilder alleleSummaryDocumentBuilder = new AlleleSummaryDocumentBuilder();
			for (AlleleSummaryDTO dto : resp.getResults()) {
				AlleleSummaryDocument doc = alleleSummaryDocumentBuilder.buildSummaryDocument(dto, resourceDescriptorPageService);
				list.add(doc);
			}
		}

		SearchResponse<AlleleSummaryDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}

}
