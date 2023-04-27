package org.alliancegenome.curation_api.controllers.crud.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkManualLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkManualLoadService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BulkManualLoadCrudController extends BaseEntityCrudController<BulkManualLoadService, BulkManualLoad, BulkManualLoadDAO> implements BulkManualLoadCrudInterface {

	@Inject
	BulkManualLoadService bulkManualLoadService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkManualLoadService);
	}

	@Override
	public ObjectResponse<BulkManualLoad> restartLoad(Long id) {
		return bulkManualLoadService.restartLoad(id);
	}
}
