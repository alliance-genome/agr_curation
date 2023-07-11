package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class BulkLoadFileExceptionService extends BaseEntityCrudService<BulkLoadFileException, BulkLoadFileExceptionDAO> {

	@Inject
	BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadFileExceptionDAO);
	}

}