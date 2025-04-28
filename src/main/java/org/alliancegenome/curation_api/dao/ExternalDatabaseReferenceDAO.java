package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ExternalDatabaseReference;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExternalDatabaseReferenceDAO extends BaseSQLDAO<ExternalDatabaseReference> {
	
	protected ExternalDatabaseReferenceDAO() {
		super(ExternalDatabaseReference.class);
	}
}
