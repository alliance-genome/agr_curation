package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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