package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadGroupCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.services.loads.BulkLoadGroupService;

@RequestScoped
public class BulkLoadGroupCrudController extends BaseEntityCrudController<BulkLoadGroupService, BulkLoadGroup, BulkLoadGroupDAO> implements BulkLoadGroupCrudInterface {

	@Inject BulkLoadGroupService bulkLoadGroupService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadGroupService);
	}

}
