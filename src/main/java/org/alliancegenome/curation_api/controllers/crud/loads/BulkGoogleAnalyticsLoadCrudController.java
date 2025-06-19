package org.alliancegenome.curation_api.controllers.crud.loads;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkGoogleAnalyticsLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkGoogleAnalyticsLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkGoogleAnalyticsLoad;
import org.alliancegenome.curation_api.services.loads.BulkGoogleAnalyticsLoadService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BulkGoogleAnalyticsLoadCrudController extends BaseEntityCrudController<BulkGoogleAnalyticsLoadService, BulkGoogleAnalyticsLoad, BulkGoogleAnalyticsLoadDAO> implements BulkGoogleAnalyticsLoadCrudInterface {
	@Inject
	BulkGoogleAnalyticsLoadService bulkGoogleAnalyticsLoadService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkGoogleAnalyticsLoadService);
	}
}
