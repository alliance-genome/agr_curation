package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.interfaces.document.TransgenicAlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.TransgenicAlleleDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.TransgenicAlleleDTO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.AlleleConstructAssociationService;

import jakarta.inject.Inject;

public class TransgenicAlleleDocumentController implements TransgenicAlleleDocumentInterface {

	@Inject
	AlleleConstructAssociationService acService;

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
