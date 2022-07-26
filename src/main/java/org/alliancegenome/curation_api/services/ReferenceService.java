package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseEntityCrudService;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.references.ReferenceSynchronisationHelper;

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

	public Reference retrieveFromLiteratureService(String curie) {
		return refSyncHelper.retrieveFromLiteratureService(curie);
	}
	
}
