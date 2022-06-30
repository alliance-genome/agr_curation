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
			ref.setDisplayXref(xrefCurie);
			ref = copyLiteratureReferenceFields(litRef, ref);
		
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
		searchField.put("cross_reference.curie", searchDetails);
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
	
	protected List<CrossReference> convertXrefs (List<LiteratureCrossReference> litXrefs) {
		
		ArrayList<CrossReference> curationXrefs = new ArrayList<CrossReference>();
		for (LiteratureCrossReference litXref : litXrefs) {
			CrossReference curationXref = new CrossReference();
			curationXref.setCurie(litXref.getCurie());
			curationXrefs.add(curationXref);
		}
		
		return curationXrefs;
	}
	
	protected Reference copyLiteratureReferenceFields(LiteratureReference litRef, Reference ref) {
		
		ref.setCurie(litRef.getCurie());
		ref.setCrossReferences(convertXrefs(litRef.getCross_references()));
		ref.setTitle(litRef.getTitle());
		
		return ref;
	}

	public void synchroniseReferences() {
		
		Pagination pagination = new Pagination();
		pagination.setLimit(500);
		int page = 0;
		Boolean allSynced = false;
		while (!allSynced) {
			pagination.setPage(page);
			SearchResponse<Reference> response = referenceDAO.findAll(pagination);
			for (Reference ref : response.getResults()) {
				LiteratureReference litRef = fetchLiteratureServiceReference(ref.getDisplayXref());
				if (litRef == null) {
					ref.setObsolete(true);
				} else {
					ref = copyLiteratureReferenceFields(litRef, ref);
				}
				referenceDAO.merge(ref);
			}
			page = page + 1;
			int nrSynced = 100 * page;
			if (nrSynced > response.getTotalResults().intValue()) {
				nrSynced = response.getTotalResults().intValue();
				allSynced = true;
			}
			log.info("Synchronised " + nrSynced + " of " + response.getTotalResults() + " Reference objects");
		}
	}
}
