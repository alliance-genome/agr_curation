package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.CurieObjectCrudController;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.ReferenceCrudInterface;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ReferenceCrudController extends CurieObjectCrudController<ReferenceService, Reference, ReferenceDAO> implements ReferenceCrudInterface {

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

	public ObjectResponse<Reference> synchroniseReference(Long id) {
		return referenceService.synchroniseReference(id);
	}
}
