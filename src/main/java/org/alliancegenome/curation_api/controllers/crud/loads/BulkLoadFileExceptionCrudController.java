package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadFileExceptionCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.services.loads.BulkLoadFileExceptionService;

@RequestScoped
public class BulkLoadFileExceptionCrudController extends BaseEntityCrudController<BulkLoadFileExceptionService, BulkLoadFileException, BulkLoadFileExceptionDAO> implements BulkLoadFileExceptionCrudInterface {

	@Inject
	BulkLoadFileExceptionService bulkLoadFileExceptionService;

	@Override
	@PostConstruct
	protected void init() {
		setService(bulkLoadFileExceptionService);
	}

}
