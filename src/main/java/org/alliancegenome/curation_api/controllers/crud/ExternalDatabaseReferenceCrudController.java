package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ExternalDatabaseReferenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.ExternalDatabaseReferenceCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExternalDatabaseReference;
import org.alliancegenome.curation_api.services.ExternalDatabaseReferenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExternalDatabaseReferenceCrudController extends BaseEntityCrudController<ExternalDatabaseReferenceService, ExternalDatabaseReference, ExternalDatabaseReferenceDAO> implements ExternalDatabaseReferenceCrudInterface {

	@Inject ExternalDatabaseReferenceService externalDatabaseReferenceService;

	@Override
	@PostConstruct
	protected void init() {
		setService(externalDatabaseReferenceService);
	}
}
