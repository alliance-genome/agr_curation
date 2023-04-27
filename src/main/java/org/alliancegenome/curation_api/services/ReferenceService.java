package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.references.ReferenceSynchronisationHelper;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ReferenceService extends BaseEntityCrudService<Reference, ReferenceDAO> {

	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceSynchronisationHelper refSyncHelper;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(referenceDAO);
	}

	public ObjectResponse<Reference> synchroniseReference(String curie) {
		return refSyncHelper.synchroniseReference(curie);
	}

	public void synchroniseReferences() {
		refSyncHelper.synchroniseReferences();
	}

	public Reference retrieveFromDbOrLiteratureService(String curieOrXref) {
		Reference reference = null;

		if (curieOrXref.startsWith("AGRKB:")) {
			reference = referenceDAO.find(curieOrXref);
		} else {
			SearchResponse<Reference> response = referenceDAO.findByField("crossReferences.referencedCurie", curieOrXref);
			List<Reference> nonObsoleteRefs = new ArrayList<>();
			if (response != null && response.getReturnedRecords() > 0) {
				response.getResults().forEach(ref -> {
					if (!ref.getObsolete())
						nonObsoleteRefs.add(ref);
				});
			}
			if (nonObsoleteRefs.size() == 1)
				reference = nonObsoleteRefs.get(0);
		}

		if (reference != null && (!reference.getObsolete() || curieOrXref.startsWith("AGRKB:")))
			return reference;

		reference = refSyncHelper.retrieveFromLiteratureService(curieOrXref);

		if (reference == null)
			return null;

		return referenceDAO.persist(reference);
	}

}
