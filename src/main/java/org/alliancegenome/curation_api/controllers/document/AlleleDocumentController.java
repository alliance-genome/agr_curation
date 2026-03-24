package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.AlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.AlleleSummaryDocumentBuilder;
import org.alliancegenome.curation_api.model.document.builders.TransgenicAlleleDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.document.es.TransgenicAlleleDTO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.associations.AlleleConstructAssociationService;

import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class AlleleDocumentController implements AlleleDocumentInterface {

	@Inject
	AlleleService alleleService;

	@Inject
	AlleleConstructAssociationService acService;

	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	@Override
	public SearchResponse<Long> getAllIds() {
		List<Long> ids = alleleService.getAllAlleleSummaryIds();
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults(ids.size());
		return response;
	}

	@Override
	public SearchResponse<AlleleSummaryDocument> findSummaryByIds(List<Long> ids) {
		SearchResponse<AlleleSummaryDocument> resp = alleleService.findAllelesForSummaryByIds(ids);
		if (resp.getResults() != null) {
			AlleleSummaryDocumentBuilder builder = new AlleleSummaryDocumentBuilder();
			for (AlleleSummaryDocument doc : resp.getResults()) {
				builder.finalizeDocument(doc, resourceDescriptorPageService);
			}
		}
		return resp;
	}

	@Override
	public SearchResponse<TransgenicAlleleDTO> findDocuments(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}
		params.put("alleleAssociationSubject.obsolete", false);
		params.put("alleleAssociationSubject.internal", false);

		SearchResponse<AlleleConstructAssociation> response = acService.findByParams(new Pagination(page, limit), params);
		List<TransgenicAlleleDTO> list = new ArrayList<>();
		if (response.getResults() != null) {
			TransgenicAlleleDocumentBuilder builder = new TransgenicAlleleDocumentBuilder();
			for (AlleleConstructAssociation association : response.getResults()) {
				TransgenicAlleleDTO doc = builder.buildTransgenicAlleleDocument(association);
				list.add(doc);
			}
		}
		SearchResponse<TransgenicAlleleDTO> ret = new SearchResponse<>(list);
		ret.setTotalResults(response.getTotalResults());
		return ret;
	}

}