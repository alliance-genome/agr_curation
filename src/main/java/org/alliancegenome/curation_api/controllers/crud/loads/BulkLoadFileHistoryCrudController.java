package org.alliancegenome.curation_api.controllers.crud.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadFileHistoryCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.loads.BulkLoadFileHistoryService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BulkLoadFileHistoryCrudController extends BaseEntityCrudController<BulkLoadFileHistoryService, BulkLoadFileHistory, BulkLoadFileHistoryDAO> implements BulkLoadFileHistoryCrudInterface {

	@Inject
	BulkLoadFileHistoryService bulkLoadFileHistoryService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadFileHistoryService);
	}

}
