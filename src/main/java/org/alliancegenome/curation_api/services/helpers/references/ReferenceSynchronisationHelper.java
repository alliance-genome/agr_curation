package org.alliancegenome.curation_api.services.helpers.references;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureCrossReference;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class ReferenceSynchronisationHelper {

	@Inject ReferenceDAO referenceDAO;
	@Inject LiteratureReferenceDAO literatureReferenceDAO;
	@Inject CrossReferenceDAO crossReferenceDAO;
	
	public Reference retrieveFromLiteratureService(String curie) {
		
		LiteratureReference litRef = fetchLiteratureServiceReference(curie);
		
		if (litRef != null) {
			Reference ref = referenceDAO.find(litRef.getCurie());
			if (ref == null)
				ref = new Reference();
			ref = copyLiteratureReferenceFields(litRef, ref);
		
			return ref;
		}
		return null;
	}
	
	protected LiteratureReference fetchLiteratureServiceReference(String curie) {
		HashMap<String, String> searchDetails = new HashMap<>();
		searchDetails.put("tokenOperator", "AND");
		searchDetails.put("queryString", curie);
		
		HashMap<String, Object> searchField = new HashMap<>();
		if (curie.startsWith("AGR")) {
			searchField.put("curie", searchDetails);
		} else {
			searchField.put("cross_references.curie", searchDetails);
		}
			
		HashMap<String, Object> filter = new HashMap<>();
		filter.put("curieFilter", searchField);
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("searchFilters", filter);		

		Pagination pagination = new Pagination(0, 50);
		SearchResponse<LiteratureReference> response = literatureReferenceDAO.searchByParams(pagination, params);
		if (response != null) {
			for (LiteratureReference result : response.getResults()) {
				if (result.getCurie().equals(curie))
					return result;
				for (LiteratureCrossReference resultXref : result.getCross_references()) {
					if (resultXref.getCurie().equals(curie))
						return result;
				}
			}
		} else {
			return null;
		}
		
		return null;
	}
		
	public void synchroniseReferences() {
		
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		int page = 0;
		int limit = 500;
		Pagination pagination = new Pagination(page, limit);

		Boolean allSynced = false;
		while (!allSynced) {
			pagination.setPage(page);
			SearchResponse<String> response = referenceDAO.findAllIds(pagination);
			if(page == 0)
				pdh.startProcess("Reference Sync", response.getTotalResults());
			for (String refCurie : response.getResults()) {
				synchroniseReference(refCurie);
				pdh.progressProcess();
			}
			page = page + 1;
			int nrSynced = limit * page;
			if (nrSynced > response.getTotalResults().intValue()) {
				nrSynced = response.getTotalResults().intValue();
				allSynced = true;
			}
		}
		pdh.finishProcess();
	}
	
	protected Reference copyLiteratureReferenceFields(LiteratureReference litRef, Reference ref) {
		String originalCurie = ref.getCurie();
		if (!litRef.getCurie().equals(originalCurie)) {
			ref = new Reference();
			ref.setCurie(litRef.getCurie());
		}
		
		List<CrossReference> xrefs = new ArrayList<CrossReference>();
		for (LiteratureCrossReference litXref : litRef.getCross_references()) {
			CrossReference xref = crossReferenceDAO.find(litXref.getCurie());
			if (xref == null) {
				xref = new CrossReference();
				xref.setCurie(litXref.getCurie());
				xref.setInternal(false);
				xref.setObsolete(false);
				xref = crossReferenceDAO.persist(xref);
			} 
			xrefs.add(xref);
		}
		if (CollectionUtils.isNotEmpty(xrefs))
			ref.setCrossReferences(xrefs);
		
		if (!ref.getCurie().equals(originalCurie) && originalCurie != null) {
			referenceDAO.updateReferenceForeignKeys(originalCurie, ref.getCurie());
			referenceDAO.remove(originalCurie);
		}
		
		return ref;
	}
	
	@Transactional
	public ObjectResponse<Reference> synchroniseReference(String curie) {
		Reference ref = referenceDAO.find(curie);
		ObjectResponse<Reference> response = new ObjectResponse<>();
		if (ref == null) return response;
		
		LiteratureReference litRef = fetchLiteratureServiceReference(curie);
		if (litRef == null) {
			ref.setObsolete(true);
		} else {
			ref = copyLiteratureReferenceFields(litRef, ref);
		}
		
		response.setEntity(referenceDAO.persist(ref));
		
		return response;
	}
}
