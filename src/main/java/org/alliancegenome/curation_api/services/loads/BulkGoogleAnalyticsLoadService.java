package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkGoogleAnalyticsLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkGoogleAnalyticsLoad;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkGoogleAnalyticsLoadService extends BaseEntityCrudService<BulkGoogleAnalyticsLoad, BulkGoogleAnalyticsLoadDAO> {
	@Inject BulkGoogleAnalyticsLoadDAO bulkGoogleAnalyticsLoadDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkGoogleAnalyticsLoadDAO);
	}

}
