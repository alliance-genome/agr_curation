package org.alliancegenome.curation_api.services.helpers.references;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ReferenceSynchronisationHelper {

	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject LiteratureReferenceDAO literatureReferenceDAO;
	@Inject CrossReferenceDAO crossReferenceDAO;

	public Reference retrieveFromLiteratureService(String curie) {

		LiteratureReference litRef = fetchLiteratureServiceReference(curie);

		if (litRef != null) {
			Reference ref = referenceService.findByCurie(curie);
			if (ref == null) {
				ref = new Reference();
			}
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
		searchField.put("curie.keyword", searchDetails);
		searchField.put("cross_references.curie.keyword", searchDetails);

		HashMap<String, Object> filter = new HashMap<>();
		filter.put("curieFilter", searchField);

		HashMap<String, Object> params = new HashMap<>();
		params.put("searchFilters", filter);

		Pagination pagination = new Pagination(0, 50);
		SearchResponse<LiteratureReference> response = literatureReferenceDAO.searchByParams(pagination, params);
		if (response != null && response.getTotalResults() != null && response.getTotalResults() == 1) {
			return response.getSingleResult();
		}

		return null;
	}

	public void synchroniseReferences() {

		ProcessDisplayHelper pdh = new ProcessDisplayHelper();

		SearchResponse<Long> response = referenceDAO.findAllIds();
		pdh.startProcess("Reference Sync", response.getTotalResults());
		for (Long refId : response.getResults()) {
			synchroniseReference(refId);
			pdh.progressProcess();
		}
		pdh.finishProcess();
	}

	protected Reference copyLiteratureReferenceFields(LiteratureReference litRef, Reference ref) {

		ref.setCurie(litRef.getCurie());
		ref.setObsolete(false);

		List<CrossReference> xrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(litRef.getCrossReferences())) {
			for (LiteratureCrossReference litXref : litRef.getCrossReferences()) {
				SearchResponse<CrossReference> xrefResponse = crossReferenceDAO.findByField("referencedCurie", litXref.getCurie());
				CrossReference xref;
				if (xrefResponse == null || xrefResponse.getSingleResult() == null) {
					xref = new CrossReference();
					xref.setReferencedCurie(litXref.getCurie());
					xref.setDisplayName(litXref.getCurie());
					xref.setInternal(false);
					xref.setObsolete(false);
					xref = crossReferenceDAO.persist(xref);
				} else {
					xref = xrefResponse.getSingleResult();
				}
				xrefs.add(xref);
			}
		}
		if (CollectionUtils.isNotEmpty(xrefs)) {
			ref.setCrossReferences(xrefs);
		}

		ref.setShortCitation(litRef.citationShort);

		return ref;
	}

	@Transactional
	public ObjectResponse<Reference> synchroniseReference(Long id) {
		Reference ref = referenceDAO.find(id);
		ObjectResponse<Reference> response = new ObjectResponse<>();
		if (ref == null) {
			return response;
		}

		LiteratureReference litRef = fetchLiteratureServiceReference(ref.getCurie());
		if (litRef == null) {
			ref.setObsolete(true);
		} else {
			ref = copyLiteratureReferenceFields(litRef, ref);
		}

		response.setEntity(referenceDAO.persist(ref));

		return response;
	}
}
