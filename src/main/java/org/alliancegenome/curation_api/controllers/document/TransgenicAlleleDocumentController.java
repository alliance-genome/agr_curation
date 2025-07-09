package org.alliancegenome.curation_api.controllers.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.interfaces.document.TransgenicAlleleDocumentInterface;
import org.alliancegenome.curation_api.model.document.builders.TransgenicAlleleDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.TransgenicAlleleDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.associations.AlleleConstructAssociationService;

import jakarta.inject.Inject;

public class TransgenicAlleleDocumentController implements TransgenicAlleleDocumentInterface {

	@Inject
	AlleleService service;
	@Inject
	AlleleConstructAssociationService acService;

	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	@Override
	public SearchResponse<TransgenicAlleleDocument> findDocuments(Integer page, Integer limit, HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		Pagination pagination = new Pagination(page, limit);
//		SearchResponse<Allele> resp1 = service.findAllAllelesWithConstructs(pagination, params);
		SearchResponse<AlleleConstructAssociation> resp = acService.findAllAssociations(pagination, params);

		ArrayList<TransgenicAlleleDocument> list = new ArrayList<>();
		if (resp.getResults() != null) {
			TransgenicAlleleDocumentBuilder builder = new TransgenicAlleleDocumentBuilder();
			builder.setVocabularyTermDAO(vocabularyTermDAO);
			for (AlleleConstructAssociation association : resp.getResults()) {
				List<TransgenicAlleleDocument> docs = builder.buildTransgenicAlleleDocument(association);
				list.addAll(docs);
			}
		}

		SearchResponse<TransgenicAlleleDocument> ret = new SearchResponse<>(list);
		ret.setTotalResults(resp.getTotalResults());
		return ret;
	}
}
