package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.CurieObjectCrudService;
import org.alliancegenome.curation_api.services.helpers.references.ReferenceSynchronisationHelper;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ReferenceService extends CurieObjectCrudService<Reference, ReferenceDAO> {

	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceSynchronisationHelper refSyncHelper;

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

	public Reference retrieveFromDbOrLiteratureService(String curieOrXref) {
		Reference reference = null;

		if (curieOrXref.startsWith("AGRKB:")) {
			reference = referenceDAO.findByIdentifierString(curieOrXref);
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
