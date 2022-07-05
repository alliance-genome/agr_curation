package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureCrossReference;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
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
		
		LiteratureReference litRef = fetchLiteratureServiceReference(xrefCurie);

		if (litRef != null) {
			Reference ref = new Reference();
			ref.setSubmittedCrossReference(xrefCurie);
			ref = copyLiteratureReferenceFields(litRef, ref, xrefCurie);
		
			return referenceDAO.persist(ref);
		}
		
		return null;
	}
	
	protected LiteratureReference fetchLiteratureServiceReference(String xrefCurie) {
		Pagination pagination = new Pagination();
		pagination.setPage(0);
		pagination.setLimit(2);
		
		HashMap<String, String> searchDetails = new HashMap<>();
		searchDetails.put("tokenOperator", "AND");
		searchDetails.put("queryString", xrefCurie);
		
		HashMap<String, Object> searchField = new HashMap<>();
		searchField.put("cross_references.curie", searchDetails);
		searchField.put("curie", searchDetails);

		HashMap<String, Object> filter = new HashMap<>();
		filter.put("cross_referenceFilter", searchField);
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("searchFilters", filter);		
		
		SearchResponse<LiteratureReference> response = literatureReferenceDAO.searchByParams(pagination, params);
		
		if (response != null) {
			for (LiteratureReference result : response.getResults()) {
				for (LiteratureCrossReference xref : result.getCross_references()) {
					if (xref.getCurie().equals(xrefCurie)) {
						return result;
					}
				}
			}
		}
		
		return null;
	}
	
	protected Reference copyLiteratureReferenceFields(LiteratureReference litRef, Reference ref, String searchCurie) {
		
		ref.setCurie(litRef.getCurie());
		
		ArrayList<String> otherXrefs = new ArrayList<String>();
		for (LiteratureCrossReference litXref : litRef.getCross_references()) {
			if (litXref.getCurie().startsWith("PMID:")) {
				ref.setPrimaryCrossReference(litXref.getCurie());
			} else {
				otherXrefs.add(litXref.getCurie());	
			} 
		}
		
		Collections.sort(otherXrefs);	
		if (ref.getPrimaryCrossReference() == null)
			ref.setPrimaryCrossReference(otherXrefs.remove(0));
		
		ref.setSecondaryCrossReferences(otherXrefs);
		
		return ref;
	}

	public void synchroniseReferences() {
		
		Pagination pagination = new Pagination();
		int limit = 500;
		pagination.setLimit(limit);
		int page = 0;
		Boolean allSynced = false;
		while (!allSynced) {
			pagination.setPage(page);
			SearchResponse<Reference> response = referenceDAO.findAll(pagination);
			for (Reference ref : response.getResults()) {
				synchroniseReference(ref.getSubmittedCrossReference());
			}
			page = page + 1;
			int nrSynced = limit * page;
			if (nrSynced > response.getTotalResults().intValue()) {
				nrSynced = response.getTotalResults().intValue();
				allSynced = true;
			}
			log.info("Synchronised " + nrSynced + " of " + response.getTotalResults() + " Reference objects");
		}
	}
	
	@Transactional
	public ObjectResponse<Reference> synchroniseReference(String submittedXref) {
		Reference ref = referenceDAO.find(submittedXref);
		LiteratureReference litRef = fetchLiteratureServiceReference(submittedXref);
		
		if (litRef == null) {
			// Set as obsolete if no longer in Literature Service
			ref.setObsolete(true);
		} else {
			ref = copyLiteratureReferenceFields(litRef, ref, submittedXref);
		}
		
		ref = referenceDAO.merge(ref);
		ObjectResponse<Reference> response = new ObjectResponse<>();
		response.setEntity(ref);
		return response;
	}
}
