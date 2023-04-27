package org.alliancegenome.curation_api.controllers.crud.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkFMSLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkFMSLoadService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BulkFMSLoadCrudController extends BaseEntityCrudController<BulkFMSLoadService, BulkFMSLoad, BulkFMSLoadDAO> implements BulkFMSLoadCrudInterface {

	@Inject
	BulkFMSLoadService bulkFMSLoadService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkFMSLoadService);
	}

	@Override
	public ObjectResponse<BulkFMSLoad> restartLoad(Long id) {
		return bulkFMSLoadService.restartLoad(id);
	}

}
