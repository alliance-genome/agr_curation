package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.ReferenceSynchronisationHelper;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ReferenceService extends BaseEntityCrudService<Reference, ReferenceDAO> {

	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceSynchronisationHelper refSyncHelper;

	Integer referenceRequest = 0;
	Date referenceRequestShallow;
	// SCRUM-6219: cache Reference *ids*, not entities. A load runs many per-record
	// transactions in one request scope, so a cached Reference entity is detached in
	// later transactions; indexing the owning Allele then fails to lazily initialize
	// Reference.crossReferences ("no session") and the load rolls back. Caching ids and
	// re-fetching per transaction keeps every Reference attached to the current session.
	HashMap<String, Long> referenceCacheMap = new HashMap<>();
	HashMap<String, Reference> shallowReferenceCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(referenceDAO);
	}

	public ObjectResponse<Reference> synchroniseReference(Long id) {
		return refSyncHelper.synchroniseReference(id);
	}

	public void synchroniseReferences() {
		refSyncHelper.synchroniseReferences();
	}

	@Override
	public ObjectResponse<Reference> getByCurie(String curie) {
		Reference reference = retrieveFromDbOrLiteratureService(curie);
		ObjectResponse<Reference> ret = new ObjectResponse<Reference>(reference);
		return ret;
	}

	@Transactional
	public Reference retrieveFromDbOrLiteratureService(String curieOrXref) {
		Reference reference = null;
		// Currently 3/10/2025 there is 1 allele with ~3200 references
		// TODO: come up with a better caching solution than this
		
		if (referenceRequest > 3500) {
			cacheReferences();
		}
		
		if (referenceCacheMap.containsKey(curieOrXref)) {
			Long cachedId = referenceCacheMap.get(curieOrXref);
			// Negative cache hit: previously resolved to no reference.
			if (cachedId == null) {
				return null;
			}
			// Re-fetch by id so the Reference is attached to the current transaction's
			// session (a cached entity would be detached). find() returns null if the id
			// was never committed (e.g. a rolled-back create) — fall through and re-resolve.
			reference = referenceDAO.find(cachedId);
			if (reference != null) {
				return reference;
			}
		}

		Log.debug("Reference not cached, caching reference: (" + curieOrXref + ")");
		reference = findOrCreateReference(curieOrXref);
		referenceCacheMap.put(curieOrXref, reference == null ? null : reference.getId());
		referenceRequest++;

		return reference;
	}
	
	public void cacheReferences() {
		if (referenceCacheMap.isEmpty()) {
			// SCRUM-6219: cache ids only (entities would be detached in later per-record
			// transactions). Re-fetched per lookup in retrieveFromDbOrLiteratureService.
			for (Map.Entry<String, Reference> entry : referenceDAO.getReferenceMap(true).entrySet()) {
				referenceCacheMap.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().getId());
			}
		}
	}

	@Transactional
	public Reference retrieveShallowReferenceFromDbOrLiteratureService(String curieOrXref) {
		Reference reference = null;
		if (shallowReferenceCacheMap.containsKey(curieOrXref)) {
			reference = shallowReferenceCacheMap.get(curieOrXref);
		} else {
			Log.debug("Reference not cached, caching reference: (" + curieOrXref + ")");
			if (shallowReferenceCacheMap.isEmpty()) {
				shallowReferenceCacheMap = referenceDAO.getReferenceMap(false);
				reference = shallowReferenceCacheMap.get(curieOrXref);
			} else {
				reference = findOrCreateReference(curieOrXref);
				referenceCacheMap.put(curieOrXref, reference == null ? null : reference.getId());
			}
		}
		return reference;
	}

	private Reference findOrCreateReference(String curieOrXref) {
		Reference reference = null;

		if (curieOrXref.startsWith("AGRKB:")) {
			reference = findByCurie(curieOrXref);
		} else {
			SearchResponse<Reference> response = referenceDAO.findByField("crossReferences.referencedCurie", curieOrXref);
			List<Reference> nonObsoleteRefs = new ArrayList<>();
			if (response != null && response.getReturnedRecords() > 0) {
				response.getResults().forEach(ref -> {
					if (!ref.getObsolete()) {
						nonObsoleteRefs.add(ref);
					}
				});
			}
			if (nonObsoleteRefs.size() == 1) {
				reference = nonObsoleteRefs.get(0);
			}
		}

		if (reference != null && (!reference.getObsolete() || curieOrXref.startsWith("AGRKB:"))) {
			return reference;
		}

		reference = refSyncHelper.retrieveFromLiteratureService(curieOrXref);

		if (reference == null) {
			return null;
		}

		return referenceDAO.persist(reference);
	}

}
