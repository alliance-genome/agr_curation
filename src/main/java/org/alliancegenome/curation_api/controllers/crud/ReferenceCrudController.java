package org.alliancegenome.curation_api.controllers.crud;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.ReferenceCrudInterface;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ReferenceCrudController extends BaseEntityCrudController<ReferenceService, Reference, ReferenceDAO> implements ReferenceCrudInterface {

	@Inject
	ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setService(referenceService);
	}

	public void synchroniseReferences() {
		referenceService.synchroniseReferences();
	}

	public ObjectResponse<Reference> synchroniseReference(String curie) {
		return referenceService.synchroniseReference(curie);
	}
}
