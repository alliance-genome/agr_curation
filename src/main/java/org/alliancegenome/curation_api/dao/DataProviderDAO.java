package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DataProvider;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DataProviderDAO extends BaseSQLDAO<DataProvider> {

	protected DataProviderDAO() {
		super(DataProvider.class);
	}
}
