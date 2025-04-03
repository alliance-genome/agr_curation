package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.ExternalDatabaseReferenceDAO;
import org.alliancegenome.curation_api.model.entities.ExternalDatabaseReference;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExternalDatabaseReferenceService extends BaseEntityCrudService<ExternalDatabaseReference, ExternalDatabaseReferenceDAO> {
	
	@Inject ExternalDatabaseReferenceDAO externalDatabaseReferenceDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(externalDatabaseReferenceDAO);
	}
}