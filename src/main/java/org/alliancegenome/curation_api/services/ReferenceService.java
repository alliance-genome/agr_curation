package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureCrossReference;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;

@RequestScoped
public class ReferenceService extends BaseCrudService<Reference, ReferenceDAO> {

	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	LiteratureReferenceDAO literatureReferenceDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(referenceDAO);
	}
	
	@Transactional
	public Reference retrieveFromLiteratureService(String xrefCurie) {
		Pagination pagination = new Pagination();
		pagination.setPage(0);
		pagination.setLimit(2);
		
		HashMap<String, String> searchDetails = new HashMap<>();
		searchDetails.put("tokenOperator", "AND");
		searchDetails.put("queryString", xrefCurie);
		
		HashMap<String, Object> searchField = new HashMap<>();
		searchField.put("cross_reference.curie", searchDetails);
		searchField.put("curie", searchDetails);

		HashMap<String, Object> filter = new HashMap<>();
		filter.put("cross_referenceFilter", searchField);
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("searchFilters", filter);		
		
		SearchResponse<LiteratureReference> response = literatureReferenceDAO.searchByParams(pagination, params);
		
		if (response != null) {
			for (LiteratureReference result : response.getResults()) {
				for (LiteratureCrossReference xref : result.getCross_reference()) {
					if (xref.getCurie().equals(xrefCurie)) {
						SearchResponse<Reference> referenceResponse = referenceDAO.findByField("displayXref", result.getCurie());
						Reference reference;
						if (referenceResponse == null) {
							reference = new Reference();
						} else {
							reference = referenceResponse.getSingleResult();
						}
						
						reference.setDisplayXref(xrefCurie);
						reference.setCurie(result.getCurie());
						reference.setCrossReferences(convertXrefs(result.getCross_reference()));
						reference.setTitle(result.getTitle());
						
						return referenceDAO.persist(reference);		
					}
				}
			}
		}
		
		return null;
	}
	
	protected List<CrossReference> convertXrefs (List<LiteratureCrossReference> litXrefs) {
		ArrayList<CrossReference> curationXrefs = new ArrayList<CrossReference>();
		for (LiteratureCrossReference litXref : litXrefs) {
			CrossReference curationXref = new CrossReference();
			curationXref.setCurie(litXref.getCurie());
			curationXrefs.add(curationXref);
		}
		
		return curationXrefs;
	}
}
