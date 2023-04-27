package org.alliancegenome.curation_api.controllers.crud.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadGroupCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.services.loads.BulkLoadGroupService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BulkLoadGroupCrudController extends BaseEntityCrudController<BulkLoadGroupService, BulkLoadGroup, BulkLoadGroupDAO> implements BulkLoadGroupCrudInterface {

	@Inject
	BulkLoadGroupService bulkLoadGroupService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadGroupService);
	}

}
