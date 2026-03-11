package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.PredictedVariantConsequenceDAO;
import org.alliancegenome.curation_api.interfaces.document.VariantDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.VariantDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.VariantSummaryDocument;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VariantService;
import org.hibernate.Hibernate;

import jakarta.inject.Inject;

public class VariantDocumentController implements VariantDocumentInterface {

	@Inject
	VariantService service;

	@Inject
	PredictedVariantConsequenceDAO predictedVariantConsequenceDAO;

	@Override
	public SearchResponse<VariantSummaryDocument> findDocuments(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
		SearchResponse<Variant> resp = service.findByParams(pagination, params);

		ArrayList<VariantSummaryDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			Map<Long, PredictedVariantConsequence> pvcById = new HashMap<>();
			VariantDocumentBuilder builder = new VariantDocumentBuilder();
			for (Variant variant : resp.getResults()) {
				Hibernate.initialize(variant.getRelatedNotes());
				for (var cvgla : variant.getCuratedVariantGenomicLocations()) {
					Hibernate.initialize(cvgla.getPredictedVariantConsequences());
					if (cvgla.getPredictedVariantConsequences() != null) {
						for (PredictedVariantConsequence pvc : cvgla.getPredictedVariantConsequences()) {
							if (pvc.getId() != null && pvc.getVariantTranscript() != null) {
								pvcById.put(pvc.getId(), pvc);
							}
						}
					}
				}
				List<VariantSummaryDocument> docs = builder.buildVariantDocument(variant);
				list.addAll(docs);
			}
			predictedVariantConsequenceDAO.populateIntronExonLocations(pvcById);
		}

		SearchResponse<VariantSummaryDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}

	@Override
	public ObjectListResponse<String> getAllVariantNames() {
		return service.findAllVariantNames();
	}
}
