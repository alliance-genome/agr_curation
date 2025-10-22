package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.document.AlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.AlleleSummaryDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDTO;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.input.Pagination;
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
	public SearchResponse<AlleleSummaryDocument> findSummary(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);

		// Check if cursor-based pagination is requested via params
		if (params.containsKey("cursor") && params.get("cursor") != null) {
			Long cursor = null;
			try {
				// Handle different cursor parameter types (String, Number, etc.)
				Object cursorParam = params.get("cursor");
				if (cursorParam instanceof String) {
					cursor = Long.parseLong((String) cursorParam);
				} else if (cursorParam instanceof Number) {
					cursor = ((Number) cursorParam).longValue();
				}
				if (cursor != null) {
					pagination.setCursor(cursor);
				}
			} catch (NumberFormatException e) {
				log.info("[CONTROLLER] Invalid cursor parameter: " + params.get("cursor"));
			}
		}

		SearchResponse<AlleleSummaryDTO> resp = alleleService.findAllelesForSummary(pagination, params);
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

		// Pass through nextCursor for cursor-based pagination
		if (resp.getNextCursor() != null) {
			ret.setNextCursor(resp.getNextCursor());
		}
		return ret;
	}

	/**
	 * Cursor-based pagination endpoint for optimal performance with large datasets.
	 * Use the nextCursor from the previous response as the cursor parameter for the next page.
	 *
	 * Example usage:
	 * 1. First request: POST /allele/document/summary/cursor?limit=10 (no cursor)
	 * 2. Next request: POST /allele/document/summary/cursor?limit=10&cursor=696355
	 */
	@Override
	public SearchResponse<AlleleSummaryDocument> findSummaryWithCursor(Integer page, Integer limit, Long cursor, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		// Add cursor to params if provided
		if (cursor != null) {
			params.put("cursor", cursor);
		}

		// Delegate to the main method
		return findSummary(page, limit, params);
	}

}
