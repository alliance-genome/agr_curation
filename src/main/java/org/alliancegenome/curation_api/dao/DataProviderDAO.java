package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DataProvider;

@ApplicationScoped
public class DataProviderDAO extends BaseSQLDAO<DataProvider> {

	protected DataProviderDAO() {
		super(DataProvider.class);
	}
}
